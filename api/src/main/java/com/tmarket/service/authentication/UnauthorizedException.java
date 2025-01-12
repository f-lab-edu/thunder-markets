package com.tmarket.service.authentication;

public class UnauthorizedException extends RuntimeException {

    // 인증실패에 대한 전역 예외 처리
    public UnauthorizedException(String message) {
        super(message);
    }
}
