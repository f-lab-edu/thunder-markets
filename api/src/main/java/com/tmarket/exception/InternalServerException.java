package com.tmarket.exception;

import lombok.Getter;

// 내부 서버 오류
@Getter
public class InternalServerException extends RuntimeException {

    public InternalServerException(String message) {
        super(message);
    }
}
