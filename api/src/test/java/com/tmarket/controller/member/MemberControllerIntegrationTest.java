package com.tmarket.controller.member;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // 내장 Ramdom Port 사용
public class MemberControllerIntegrationTest {

    @LocalServerPort // port 주입.
    private int port;

    @Autowired  // TestRestTemplate 사용
    private TestRestTemplate restTemplate;

    @BeforeEach // 테스트 초기화
    public void setup() {
        // HttpComponentsClientHttpRequestFactory 사용 설정
        restTemplate.getRestTemplate()
                .setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        // 사용자 정의 ErrorHandler 설정
        restTemplate.getRestTemplate().setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return false; // 모든 응답을 에러로 간주하지 않음
            }
        });
    }

    @Test
    public void testValidLogin() {
        String loginUrl = "http://localhost:" + port + "/auth/login";

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("cbkdevelop57@gmail.com");
        loginRequest.setPassword("passwordTest1234!@");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginRequest> request = new HttpEntity<>(loginRequest, headers);

        ResponseEntity<LoginResponse> response = restTemplate.postForEntity(loginUrl, request, LoginResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("로그인 성공");
    }
}