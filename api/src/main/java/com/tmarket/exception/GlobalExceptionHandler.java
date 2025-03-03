package com.tmarket.exception;

import com.tmarket.model.member.LoginDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

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

    // 500 기타 모든 예외 처리 (Exception)
    @ExceptionHandler({InternalServerException.class, Exception.class})
    public ResponseEntity<String> handleGlobalException(Exception ex) {
        logger.error("예상치 못한 서버 오류 발생: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("예상치 못한 오류가 발생했습니다.");
    }
}
