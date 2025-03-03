package com.tmarket.service.authentication;

import com.tmarket.exception.IncorrectPasswordException;
import com.tmarket.exception.UserNotFoundException;
import com.tmarket.model.member.LoginDTO;
import com.tmarket.model.member.User;
import com.tmarket.model.member.UserDTO;
import com.tmarket.repository.member.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
   private final Map<String, UserDTO> userInfoStore = new ConcurrentHashMap<>();
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(); // Spring Security에서는 BCrypt.checkpw() 대신 BCryptPasswordEncoder 사용을 권장힘
    private final Key accessTokenKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final Key refreshTokenKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final Date expirationDate = new Date(System.currentTimeMillis() + 604800000);

    public AuthenticationServiceImpl() { // 생성자로 DTO에 하드코딩 데이터 주입
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
                .setSubject(user.getEmail())
                .claim("name", user.getUserName())
                .setExpiration(expirationDate) // AccessToken 유효기간: 7일
                .signWith(accessTokenKey, SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = Jwts.builder()
                .setSubject(user.getEmail())
                .setExpiration(expirationDate) // RefreshToken 유효기간: 14일
                .signWith(refreshTokenKey, SignatureAlgorithm.HS256)
                .compact();
        logger.info("로그인 유저 AccessToken: {}", accessToken);
        logger.info("로그인 유저 RefreshToken: {}", refreshToken);

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
}
