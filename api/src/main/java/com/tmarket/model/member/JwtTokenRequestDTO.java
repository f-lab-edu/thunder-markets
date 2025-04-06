package com.tmarket.model.member;

import lombok.Data;

@Data
public class JwtTokenRequestDTO {
    private String accessToken;
    private String refreshToken;
}
