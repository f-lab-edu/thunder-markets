package com.tmarket.service.authentication;

import com.tmarket.model.member.LoginDTO;

public interface AuthenticationService {
    LoginDTO.LoginResponse authenticateUser(LoginDTO.LoginRequest request);

    Long validateTokenAndGetUserId(String token);
}
