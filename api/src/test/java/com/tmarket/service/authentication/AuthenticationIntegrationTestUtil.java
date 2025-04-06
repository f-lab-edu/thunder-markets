package com.tmarket.service.authentication;

import com.tmarket.model.member.LoginDTO;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationIntegrationTestUtil {

    @Autowired
    private TestRestTemplate restTemplate;
    @LocalServerPort
    private int port;
    private String loginUrl;
    private String accessToken; // 테스트 코드에서 사용할 accessToken

    @PostConstruct
    void setupToken() {
        this.loginUrl = "http://localhost:" + port + "/auth/login";
        System.out.println("BaseLogin URL: " + loginUrl);

        this.accessToken = getAccessToken(
                "cbkdevelop57@gmail.com",
                "passwordTest1234!@"
        );
        System.out.println("인증 토큰 초기화 완료: " + accessToken);
    }

    public String getAccessToken(String email, String password) {
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
    public HttpHeaders createAuthHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken); // 여기서 Bearer가 포함됨
        return headers;
    }
    public HttpHeaders getAuthHeaders() {
        return createAuthHeaders(accessToken);
    }

    // 인증 헤더와 본문 생성
    public <T> HttpEntity<T> createAuthEntityWithBody(T body, String accessToken) {
        return new HttpEntity<>(body, createAuthHeaders(accessToken));
    }
    public <T> HttpEntity<T> getAuthEntity(T body) {
        return createAuthEntityWithBody(body, accessToken);
    }
}
