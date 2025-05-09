package com.tmarket.controller.member;

import com.tmarket.model.member.JwtTokenRequestDTO;
import com.tmarket.model.member.LoginDTO;
import com.tmarket.model.member.LoginDTO.LoginRequest;
import com.tmarket.model.member.LoginDTO.LoginResponse;
import com.tmarket.service.authentication.AuthenticationService;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value= {"/auth", "/", ""})
public class MemberController {

    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);

    private AuthenticationService authenticationService;

    @Autowired
    public MemberController(
            AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(
            @RequestBody LoginRequest request,
            HttpServletResponse httpServletResponse) throws RuntimeException {

        logger.info("로그인 요청: {}", request.toString());

        LoginDTO.LoginResponse response = authenticationService.authenticateUser(request);
        httpServletResponse.setHeader("Authorization", "Bearer " + response.getAccessToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reAuthenticate")
    public ResponseEntity<LoginDTO.LoginResponse> reAuthenticateUser(@RequestBody JwtTokenRequestDTO request) {
        LoginDTO.LoginResponse response = authenticationService.reAuthenticateUser(
                request.getAccessToken(),
                request.getRefreshToken()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<LoginResponse> logoutUser(
            @RequestBody LoginRequest request) throws RuntimeException {

        logger.info("로그인 요청: {}", request.getClass());

        return null;
    }
}
