package com.tmarket.exception;

import lombok.Getter;

// 잘못된 요청 예외
@Getter
public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException(String message) {
        super(message);
    }
}
