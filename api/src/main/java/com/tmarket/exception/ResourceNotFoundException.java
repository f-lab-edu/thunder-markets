package com.tmarket.exception;

import lombok.Getter;

// 리소스 없음 예외
@Getter
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
