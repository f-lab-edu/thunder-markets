package com.tmarket.controller.member;

import com.tmarket.model.member.UserDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping(value= {"/auth", "/", ""})
public class MemberController {

    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);
    private final Map<String, UserDTO> userInfoStore = new ConcurrentHashMap<>();
//    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);


    public MemberController() { // 하드코딩 데이터
        String hashedPassword = BCrypt.hashpw("passwordTest1234!@", BCrypt.gensalt());
        logger.info("비밀번호 해시값: " + hashedPassword);

        UserDTO user = new UserDTO(
                "1", "최병권", "cbkdevelop57@gmail.com", hashedPassword,
                 "ACT", new Date(), new Date(), new Date(), true
        );
        userInfoStore.put(user.getEmail(), user);
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginRequest request, HttpServletRequest httpServletRequest, HttpSession session) throws RuntimeException {

        logger.info("===================================================");
        logger.info("===================================================");
        logger.info("===================================================");
        logger.info("========thunder-market 에 오신것을 환영합니다.========");
        logger.info("===================================================");
        logger.info("===================================================");
        logger.info("===================================================");

        // 로그인 입력값 조회
        UserDTO user = userInfoStore.get(request.getEmail());

        // 사용자 확인
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse("아이디 혹은 이메일이 유효하지 않습니다.", null, null, null));
        }

        // 비밀번호 확인
        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse("아이디 혹은 이메일이 유효하지 않습니다.", null, null, null));
        }

        // 마지막 로그인 날짜 설정
        user.setLastDt(new Date());

        // 세션 설정
        session = httpServletRequest.getSession(false); // 기존 세션 가져오기
        if (session != null) {
            session.invalidate(); // 기존 세션 무효화
        }
        session = httpServletRequest.getSession(true); // 새로운 세션 생성
        session.setAttribute("user", user); // 세션에 사용자 정보 저장
        session.setMaxInactiveInterval(86400000); // 세션 유지 시간: 하루
        UserDTO sessionUser = (UserDTO) session.getAttribute("user");
        logger.info("로그인 유저 세션값 정보: {}", sessionUser);

        // JWT 토큰 발행
        /*String token = Jwts.builder()
                .setSubject(user.getEmail())
                .claim("name", user.getName())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 토큰 유효기간 : 하루
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
        return new LoginResponse(token, user.getName(), user.getEmail(), user.getLastDt());*/

        return ResponseEntity.ok()
                .body(new LoginResponse("로그인 성공", user.getName(), user.getEmail(), user.getLastDt()));
    }
}

@Getter
@Setter
class LoginRequest {
    private String email;
    private String password;
}

@Getter
class LoginResponse {
    private String message;
    private String name;
    private String email;
    private Date lastDt;

    public LoginResponse(String message, String name, String email, Date lastDt) {
        this.message = message;
        this.name = name;
        this.email = email;
        this.lastDt = lastDt;
    }
}
