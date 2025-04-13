package com.tmarket.service.authentication;

import com.tmarket.model.member.LoginDTO;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

public class AuthenticationIntegrationTestUtil {

    public static String getAccessToken(TestRestTemplate restTemplate, String loginUrl, String email, String password) {
        LoginDTO.LoginRequest request = new LoginDTO.LoginRequest();
        request.setEmail(email);
        request.setPassword(password);

        ResponseEntity<LoginDTO.LoginResponse> response = restTemplate.postForEntity(
                loginUrl, request, LoginDTO.LoginResponse.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody().getAccessToken(); // 엑세스 토큰에 Bearer가 포함되어 있음
        }
        throw new RuntimeException("토큰 발급 실패");
    }

    // 인증 헤더 생성
    public static HttpHeaders createAuthHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken); // 여기서 Bearer가 포함됨
        return headers;
    }

    // 인증 헤더와 본문 생성
    public static <T> HttpEntity<T> createAuthEntityWithBody(T body, String accessToken) {
        return new HttpEntity<>(body, createAuthHeaders(accessToken));
    }
}
