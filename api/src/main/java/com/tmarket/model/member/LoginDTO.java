package com.tmarket.model.member;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

public class LoginDTO {
    // 기본 생성자를 private으로 선언하여 인스턴스 생성 방지
    // 정적 중첩 클래스만을 포함하고 있으므로 인스턴스를 생성할 필요가 없음
    private LoginDTO() {
        throw new UnsupportedOperationException("유틸리티 클래스이므로 인스턴스를 생성할 수 없습니다.");
    }

    @Getter
    @Setter
    public static class LoginRequest {
        private String email;
        private String password;
    }

    @Getter
    public static class LoginResponse {
        private String token;
        private String name;
        private String email;
        private Date lastDt;

        public LoginResponse(String token, String name, String email, Date lastDt) {
            this.token = token;
            this.name = name;
            this.email = email;
            this.lastDt = lastDt;
        }
    }
}
