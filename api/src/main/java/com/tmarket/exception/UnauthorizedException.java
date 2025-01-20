package com.tmarket.exception;

// 인증실패에 대한 전역 예외 처리
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
