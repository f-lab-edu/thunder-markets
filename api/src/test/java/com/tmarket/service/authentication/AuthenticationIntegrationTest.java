package com.tmarket.service.authentication;

import com.tmarket.model.conf.PropertyConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationIntegrationTest {

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    protected PropertyConfig propertyConfig;

    @LocalServerPort
    protected int port;

    protected String loginUrl;

    protected String accessToken; // 테스트 코드에서 사용할 accessToken

    @BeforeAll
    void setupToken() {
        loginUrl = "http://localhost:" + port + propertyConfig.getAuthLogintUrl();
        System.out.println("BaseLogin URL: " + loginUrl);

        accessToken = AuthenticationIntegrationTestUtil.getAccessToken(
                restTemplate,
                loginUrl,
                "cbkdevelop57@gmail.com",
                "passwordTest1234!@"
        );
        System.out.println("인증 토큰 초기화 완료: " + accessToken);
    }

    // 인증 헤더 생성
    protected HttpHeaders getAuthHeaders() {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setBearerAuth(accessToken);
//        headers.set("Authorization", "Bearer " + accessToken);
//        return headers;
        return AuthenticationIntegrationTestUtil.createAuthHeaders(accessToken);
    }

    // 인증 헤더만 포함
    protected HttpEntity<?> getAuthEntity() {
        return new HttpEntity<>(getAuthHeaders());
    }

    // 인증 헤더와 본문 포함
    protected <T> HttpEntity<T> getAuthEntity(T body) {
        return new HttpEntity<>(body, getAuthHeaders());
    }
}
