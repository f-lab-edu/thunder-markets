package com.tmarket.exception;

import com.tmarket.model.member.LoginDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

// @ControllerAdvice : 전역 예외 핸들러 매핑 클래스
// basePackages : 특정 패키지 지정
// assignableTypes  : 특정 클래스 지정
@ControllerAdvice(basePackages = {"com.tmarket.controller"})
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 401 Unauthorized 예외 처리 (UnauthorizedException)
    @ExceptionHandler({UserNotFoundException.class, IncorrectPasswordException.class})
    public ResponseEntity<LoginDTO.LoginResponse> handleUnauthorizedException(UnauthorizedException ex) {
        logger.error("인증 실패: {}", ex.getMessage());
        LoginDTO.LoginResponse response = LoginDTO.LoginResponse.builder()
                .accessToken(null)
                .refreshToken(null)
                .expirationDate(null)
                .userId(null)
                .name(null)
                .email(null)
                .memberStatus(null)
                .registDate(null)
                .modifyDate(null)
                .lastLoginDate(null)
                .isActive(null)
                .errorMessage(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // 400 Bad Request 예외 처리 (InvalidRequestException)
    @ExceptionHandler({InvalidRequestException.class, IllegalArgumentException.class})
    public ResponseEntity<LoginDTO.LoginResponse> handleInvalidRequestException(Exception ex) {
        logger.error("잘못된 요청: {}", ex.getMessage());
        LoginDTO.LoginResponse response = LoginDTO.LoginResponse.builder()
                .accessToken(null)
                .refreshToken(null)
                .expirationDate(null)
                .userId(null)
                .name(null)
                .email(null)
                .memberStatus(null)
                .registDate(null)
                .modifyDate(null)
                .lastLoginDate(null)
                .isActive(null)
                .errorMessage(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler({
            JwtExceptions.TokenExpiredException.class,
            JwtExceptions.UnsupportedTokenException.class,
            JwtExceptions.MalformedTokenException.class,
            JwtExceptions.InvalidSignatureException.class,
            JwtExceptions.InvalidTokenException.class
    })
    public ResponseEntity<String> handleJwtExceptions(UnauthorizedException ex) {
        logger.error("JWT 예외 발생: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    // 500 기타 모든 예외 처리 (Exception) - Internal Server Error (좀 더 구체적으로 메시지 처리)
    @ExceptionHandler({InternalServerException.class, Exception.class})
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception ex) {
        logger.error("예상치 못한 서버 오류 발생: {}", ex); // 예외 전체 스택 트레이스

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("message", "예상치 못한 오류가 발생했습니다.");
        errorDetails.put("error", ex.getClass().getSimpleName()); // 발생한 예외 클래스명
        errorDetails.put("details", ex.getMessage()); // 예외 상세 메시지
        errorDetails.put("stackTrace", sw.toString()); // 예외 스택 트레이스
        errorDetails.put("timestamp", new Date()); // 예외 발생 일자
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetails);
    }
}
