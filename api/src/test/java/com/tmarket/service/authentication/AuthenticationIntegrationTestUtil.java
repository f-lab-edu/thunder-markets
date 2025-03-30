package com.tmarket.service.authentication;

import com.tmarket.model.member.LoginDTO;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

public class AuthenticationIntegrationTestUtil {

    public static String getAccessToken(TestRestTemplate restTemplate, String loginUrl,
                                        String email, String password) {
        LoginDTO.LoginRequest request = new LoginDTO.LoginRequest();
        request.setEmail(email);
        request.setPassword(password);

        ResponseEntity<LoginDTO.LoginResponse> response = restTemplate.postForEntity(
                loginUrl, request, LoginDTO.LoginResponse.class);

        return response.getBody().getAccessToken();
    }

    public static HttpHeaders createAuthHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
//        headers.setBearerAuth(accessToken); // Authorization: Bearer <토큰>
        headers.set("Authorization", "Bearer " + accessToken);
        return headers;
    }

    public static HttpEntity<?> createAuthEntity(String accessToken) {
        return new HttpEntity<>(createAuthHeaders(accessToken));
    }

    public static <T> HttpEntity<T> createAuthEntityWithBody(T body, String accessToken) {
        return new HttpEntity<>(body, createAuthHeaders(accessToken));
    }
}
