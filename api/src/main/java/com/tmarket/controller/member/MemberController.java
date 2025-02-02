package com.tmarket.controller.member;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tmarket.exception.UnauthorizedException;
import com.tmarket.model.member.LoginDTO;
import com.tmarket.model.member.LoginDTO.LoginRequest;
import com.tmarket.model.member.LoginDTO.LoginResponse;
import com.tmarket.service.authentication.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
            HttpServletRequest httpServletRequest,
            HttpSession session) throws RuntimeException, IllegalArgumentException, UnauthorizedException {

        logger.info("로그인 요청: {}", request.toString());

        LoginDTO.LoginResponse response = authenticationService.authenticateUser(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<LoginResponse> logoutUser(@RequestBody LoginRequest request,
                                                   HttpServletRequest httpServletRequest, HttpSession session) throws RuntimeException, JsonProcessingException {
        logger.info("로그인 요청: {}", request.getClass());

        return null;
    }
}
