package com.tmarket.controller.member;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmarket.model.member.LoginDTO;
import com.tmarket.model.member.LoginDTO.LoginRequest;
import com.tmarket.model.member.LoginDTO.LoginResponse;
import com.tmarket.service.authentication.AuthenticationService;
import com.tmarket.service.authentication.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value= {"/auth", "/", ""})
public class MemberController {

    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);

    private ObjectMapper objectMapper;

    private AuthenticationService authenticationService;

    @Autowired
    public MemberController(
            AuthenticationService authenticationService,
            ObjectMapper objectMapper) {
        this.authenticationService = authenticationService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginRequest request,
                               HttpServletRequest httpServletRequest, HttpSession session) throws RuntimeException, JsonProcessingException {
        logger.info("로그인 요청: {}", objectMapper.writeValueAsString(request));

        try {
            // Authentication Layer로 사용자 인증 위임
            LoginDTO.LoginResponse response = authenticationService.authenticateUser(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new LoginDTO.LoginResponse(e.getMessage(), null, null, null));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginDTO.LoginResponse(e.getMessage(), null, null, null));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<LoginResponse> logoutUser(@RequestBody LoginRequest request,
                                                   HttpServletRequest httpServletRequest, HttpSession session) throws RuntimeException, JsonProcessingException {
        logger.info("로그인 요청: {}", objectMapper.writeValueAsString(request));

        try {
            // Authentication Layer로 사용자 인증 위임
            LoginDTO.LoginResponse response = authenticationService.authenticateUser(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new LoginDTO.LoginResponse(e.getMessage(), null, null, null));
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginDTO.LoginResponse(e.getMessage(), null, null, null));
        }
    }
}
