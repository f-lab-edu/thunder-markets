package com.tmarket.service.authentication;

import com.tmarket.model.member.LoginDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

public interface AuthenticationService {
    LoginDTO.LoginResponse authenticateUser(LoginDTO.LoginRequest request);

    LoginDTO.LoginResponse reAuthenticateUser(String accessToken, String refreshToken);

    String validateTokenAndGetUserId(String token);

    ResponseEntity<LoginDTO.LoginResponse> logoutUser(HttpServletRequest request);
}
