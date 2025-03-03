package com.tmarket.model.member;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

public class LoginDTO {
    // 기본 생성자를 private으로 선언하여 인스턴스 생성 방지
    // 정적 중첩 클래스만을 포함하고 있으므로 인스턴스를 생성할 필요가 없음
    // 250114 추가 throw new UnsupportedOperationException("유틸리티 클래스이므로 인스턴스를 생성할 수 없습니다.");
    // 은 유틸리티 클래스에 대한 예외이지만, 이 클래스는 유틸리티 클래스가 아니고 private이 이미 그 기능을
    // 수행하고 있기때문에 불필요하여 삭제함
    private LoginDTO() {
    }

    @Getter
    @Setter
    @ToString
    public static class LoginRequest {
        private String email;
        private String password;
    }

    @Getter
    @ToString
    @Builder
    public static class LoginResponse {
        private final String accessToken;
        private final String refreshToken;
        private final Date expirationDate;
        private final Long userId;
        private final String name;
        private final String email;
        private final String memberStatus;
        private final Date registDate;
        private final Date modifyDate;
        private final Date lastLoginDate;
        private final Boolean isActive;
        private final String errorMessage;
    }
}
