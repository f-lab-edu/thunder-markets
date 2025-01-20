package com.tmarket.service.authentication;

import com.tmarket.exception.IncorrectPasswordException;
import com.tmarket.exception.UserNotFoundException;
import com.tmarket.model.member.LoginDTO;
import com.tmarket.model.member.UserDTO;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    private final Map<String, UserDTO> userInfoStore = new ConcurrentHashMap<>();
    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public AuthenticationServiceImpl() { // 생성자로 DTO에 하드코딩 데이터 주입
        addUser("1", "최병권", "cbkdevelop57@gmail.com", "passwordTest1234!@");
        addUser("2", "홍길동", "mrhong@gmail.com", "password123!");
        addUser("3", "개코", "gaeko@gmail.com", "choija123!");
    }

    private void addUser(String userId, String name, String email, String password) {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        logger.info("비밀번호 해시값: " + hashedPassword);
        UserDTO user = new UserDTO(userId, name, email, hashedPassword,
                "ACT", new Date(), new Date(), new Date(), true);
        userInfoStore.put(user.getEmail(), user);
    }

    @Override
    public LoginDTO.LoginResponse authenticateUser(LoginDTO.LoginRequest request) {
        // 인증 로직 구현
        if (request.getEmail() == null || request.getPassword() == null
                || request.getEmail().isEmpty() || request.getPassword().isEmpty()) {
            throw new IllegalArgumentException("아이디와 비밀번호는 필수 입력값입니다.");
        }
        // 로그인 입력값 조회
        UserDTO user = userInfoStore.get(request.getEmail());

        // 사용자 확인 (예외 처리 세분화 : UnauthorizedException -> UserNotFoundException)
        if (user == null || user.getEmail().isEmpty()) {
            throw new UserNotFoundException("아이디 혹은 이메일이 유효하지 않습니다.");
        }

        // 비밀번호 확인 (예외 처리 세분화 : UnauthorizedException -> IncorrectPasswordException)
        if (request.getPassword().isEmpty() || !BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new IncorrectPasswordException("아이디 혹은 이메일이 유효하지 않습니다.");
        }

        // 마지막 로그인 날짜 설정
        user.setLastDt(new Date());

        // JWT 토큰 발행
        String token = Jwts.builder()
                .setSubject(user.getEmail())
                .claim("name", user.getName())
//                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 토큰 유효기간 : 하루
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
        logger.info("로그인 유저 토큰값 정보: {}", token);

        return new LoginDTO.LoginResponse(token, user.getName(), user.getEmail(), user.getLastDt());
    }
}

