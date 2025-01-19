package com.tmarket.exception;

public class UserNotFoundException extends UnauthorizedException {
    public UserNotFoundException(String message) {
        super(message);
    }
}