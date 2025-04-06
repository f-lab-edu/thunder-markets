package com.tmarket.service.authentication;

import com.tmarket.exception.IncorrectPasswordException;
import com.tmarket.exception.JwtExceptions;
import com.tmarket.exception.UserNotFoundException;
import com.tmarket.model.member.LoginDTO;
import com.tmarket.model.member.User;
import com.tmarket.repository.member.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private UserRepository userRepository;
    private RedisTemplate<String, String> redisTemplate;

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationServiceImpl.class);
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(); // Spring Security에서는 BCrypt.checkpw() 대신 BCryptPasswordEncoder 사용을 권장함
    private final Key accessTokenKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final Key refreshTokenKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final Date expirationDate = new Date(System.currentTimeMillis() + 604800000);
    long accessTokenExpireMs = 1000L * 60 * 60 * 24; // 1일
    long refreshTokenExpireMs = 1000L * 60 * 60 * 24 * 7; // 7일

    // Redis
    private static final String REDIS_ACCESS_TOKEN_PREFIX = "ACCESS_TOKEN:";
    private static final String REDIS_REFRESH_TOKEN_PREFIX = "REFRESH_TOKEN:";

    AuthenticationServiceImpl(UserRepository userRepository,
                              RedisTemplate<String, String> redisTemplate) {
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public LoginDTO.LoginResponse authenticateUser(LoginDTO.LoginRequest request) {
        // 인증 로직 구현
        if (request.getEmail() == null || request.getPassword() == null
                || request.getEmail().isEmpty() || request.getPassword().isEmpty()) {
            throw new IllegalArgumentException("아이디와 비밀번호는 필수 입력값입니다.");
        }
        // 로그인 입력값 조회
        User user = userRepository.findByEmail(request.getEmail());

        // 사용자 확인 (예외 처리 세분화 : UnauthorizedException -> UserNotFoundException)
        if (user == null || user.getEmail().isEmpty()) {
            throw new UserNotFoundException("아이디 혹은 이메일이 유효하지 않습니다.");
        }

        // 비밀번호 확인 (예외 처리 세분화 : UnauthorizedException -> IncorrectPasswordException)
        if (request.getPassword().isEmpty() || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IncorrectPasswordException("아이디 혹은 이메일이 유효하지 않습니다.");
        }

        // 마지막 로그인 날짜 설정
        user.setLastLoginDate(new Date());
        userRepository.save(user);

        // JWT 토큰 발행
        String accessToken = Jwts.builder()
                .setSubject(String.valueOf(user.getEmail()))
                .claim("name", user.getUserName())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpireMs))
                .signWith(accessTokenKey, SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = Jwts.builder()
                .setSubject(String.valueOf(user.getEmail()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpireMs))
                .signWith(refreshTokenKey, SignatureAlgorithm.HS256)
                .compact();
        logger.info("로그인 유저 AccessToken: {}", accessToken);
        logger.info("로그인 유저 RefreshToken: {}", refreshToken);

        redisTemplate.opsForValue().set(REDIS_ACCESS_TOKEN_PREFIX + user.getEmail(), accessToken, accessTokenExpireMs, TimeUnit.MILLISECONDS);
        redisTemplate.opsForValue().set(REDIS_REFRESH_TOKEN_PREFIX + user.getEmail(), refreshToken, refreshTokenExpireMs, TimeUnit.MILLISECONDS);

        return LoginDTO.LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expirationDate(expirationDate)
                .userId(user.getUserId())
                .name(user.getUserName())
                .email(user.getEmail())
                .memberStatus(user.getMemberStatus())
                .registDate(user.getRegistDate())
                .modifyDate(user.getModifyDate())
                .lastLoginDate(user.getLastLoginDate())
                .isActive(user.getIsActive())
                .build();
    }

    public LoginDTO.LoginResponse reAuthenticateUser(String accessToken, String refreshToken) {

        // 0. 토큰에서 Bearer 제거
        String extractToken = extractToken(accessToken);

        // 1. accessToken가 만료되었는지 확인하고 사용자 정보 추출
        String email;
        try {
            email = Jwts.parserBuilder()
                    .setSigningKey(accessTokenKey)
                    .build()
                    .parseClaimsJws(extractToken)
                    .getBody()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            // accessToken이 만료됐지만 subject는 추출 가능
            email = e.getClaims().getSubject();
        } catch (JwtException e) {
            throw new JwtExceptions.InvalidTokenException("유효하지 않은 accessToken입니다.");
        }

        // 2. Redis에서 refreshToken 확인
        String redisRefreshToken = redisTemplate.opsForValue().get(REDIS_REFRESH_TOKEN_PREFIX + email);
        if (redisRefreshToken == null || !redisRefreshToken.equals(refreshToken)) {
            throw new JwtExceptions.InvalidTokenException("리프레시 토큰이 유효하지 않습니다.");
        }

        // 3. refreshToken가 만료되었는지 여부 확인
        Claims refreshClaims;
        try {
            refreshClaims = Jwts.parserBuilder()
                    .setSigningKey(refreshTokenKey)
                    .build()
                    .parseClaimsJws(refreshToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new JwtExceptions.TokenExpiredException("리프레시 토큰이 만료되었습니다.");
        }
        // 4. refreshToken에서 만료일자 추출
        Date refreshTokenExpiration = refreshClaims.getExpiration();
        Date now = new Date();

        // 5. refreshToken 기간이 50% 초과시 재발급
        long remainTtlMs = refreshTokenExpiration.getTime() - now.getTime();
        long totalTtlMs = refreshTokenExpireMs;
        boolean needRenewRefresh = remainTtlMs < (totalTtlMs / 2);

        String newRefreshToken = refreshToken;

        if (needRenewRefresh) {
            newRefreshToken = Jwts.builder()
                    .setSubject(email)
                    .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpireMs))
                    .signWith(refreshTokenKey, SignatureAlgorithm.HS256)
                    .compact();

            redisTemplate.opsForValue().set("REFRESH_TOKEN:" + email, newRefreshToken, refreshTokenExpireMs, TimeUnit.MILLISECONDS);
        }

        // 6. 새 AccessToken 재발급
        String newAccessToken = Jwts.builder()
                .setSubject(email)
                .claim("name", userRepository.findByEmail(email).getUserName())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpireMs)) // 새 TTL
                .signWith(accessTokenKey, SignatureAlgorithm.HS256)
                .compact();

        redisTemplate.opsForValue().set(REDIS_ACCESS_TOKEN_PREFIX + email, newAccessToken, accessTokenExpireMs, TimeUnit.MILLISECONDS);

        return LoginDTO.LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .expirationDate(new Date(System.currentTimeMillis() + accessTokenExpireMs))
                .email(email)
                .build();
    }

    private String extractToken(String token) {
        return Optional.ofNullable(token)
                .filter(t -> t.startsWith("Bearer "))
                .map(t -> t.substring(7))
                .orElseThrow(() -> new JwtExceptions.InvalidTokenException("유효하지 않은 토큰입니다."));
    }

    @Override
    public String validateTokenAndGetUserId(String token) {

        // 0. 토큰에서 Bearer 제거
        String extractToken = extractToken(token);

        try {
            // 1. JWT토큰을 파싱하고 서명을 검증하여 유효성을 확인
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(accessTokenKey)
                    .build()
                    .parseClaimsJws(extractToken)
                    .getBody();

            // 2. 토큰에서 사용자 ID(email) 추출
            String email = claims.getSubject();

            // 3. Redis에서 AccessToken 조회
            String redisAccessToken = redisTemplate.opsForValue().get(REDIS_ACCESS_TOKEN_PREFIX + email);

            if (!redisAccessToken.equals(extractToken)) {
                throw new JwtExceptions.InvalidTokenException("토큰이 유효하지 않습니다.");
            } else if (redisAccessToken == null) {
                throw new JwtExceptions.TokenNeedsReissueException("토큰이 만료되었습니다.");
            }

            return email;
        } catch (ExpiredJwtException e) {
            throw new JwtExceptions.TokenExpiredException("토큰이 만료되었습니다.");
        } catch (UnsupportedJwtException e) {
            throw new JwtExceptions.UnsupportedTokenException("지원되지 않는 토큰 형식입니다.");
        } catch (MalformedJwtException e) {
            throw new JwtExceptions.MalformedTokenException("잘못된 토큰 형식입니다.");
        } catch (SignatureException e) {
            throw new JwtExceptions.InvalidSignatureException("유효하지 않은 서명입니다.");
        } catch (JwtException e) {
            throw new JwtExceptions.InvalidTokenException("유효하지 않은 토큰입니다.");
        }
    }

    @Override
    public ResponseEntity<LoginDTO.LoginResponse> logoutUser(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String extractToken = extractToken(token);

        String email = Jwts.parserBuilder()
                .setSigningKey(accessTokenKey)
                .build()
                .parseClaimsJws(extractToken)
                .getBody()
                .getSubject();

        redisTemplate.delete(REDIS_ACCESS_TOKEN_PREFIX + email);
        redisTemplate.delete(REDIS_REFRESH_TOKEN_PREFIX + email);

        return ResponseEntity.ok().build();
    }
}
