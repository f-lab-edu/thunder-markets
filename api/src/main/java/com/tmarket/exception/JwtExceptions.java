package com.tmarket.exception;

import lombok.Getter;

public class JwtExceptions {

    @Getter
    public static class TokenExpiredException extends UnauthorizedException {
        public TokenExpiredException(String message) {
            super(message);
        }
    }

    @Getter
    public static class UnsupportedTokenException extends UnauthorizedException {
        public UnsupportedTokenException(String message) {
            super(message);
        }
    }

    @Getter
    public static class MalformedTokenException extends UnauthorizedException {
        public MalformedTokenException(String message) {
            super(message);
        }
    }

    @Getter
    public static class InvalidSignatureException extends UnauthorizedException {
        public InvalidSignatureException(String message) {
            super(message);
        }
    }

    @Getter
    public static class InvalidTokenException extends UnauthorizedException {
        public InvalidTokenException(String message) {
            super(message);
        }
    }

    @Getter
    public static class TokenNeedsReissueException  extends UnauthorizedException {
        public TokenNeedsReissueException(String message) {
            super(message);
        }
    }
}
