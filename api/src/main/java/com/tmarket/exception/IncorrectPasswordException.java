package com.tmarket.exception;

public class IncorrectPasswordException extends UnauthorizedException {
    public IncorrectPasswordException(String message) {
        super(message);
    }
}
