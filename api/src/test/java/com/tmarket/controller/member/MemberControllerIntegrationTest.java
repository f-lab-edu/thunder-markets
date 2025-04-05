package com.tmarket.controller.member;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tmarket.model.member.LoginDTO;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // 내장 Random Port 사용
public class MemberControllerIntegrationTest {

    @Autowired  // TestRestTemplate 사용
    private TestRestTemplate restTemplate;

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    @LocalServerPort
    private int port;  // 🔹 테스트 실행 시 할당되는 랜덤 포트

    private String baseUrl;

    @BeforeEach // 테스트 초기화
    public void setup() {
        CloseableHttpClient httpClient = HttpClients.custom().build();
        this.restTemplate = new TestRestTemplate(
                restTemplateBuilder.requestFactory(() -> new HttpComponentsClientHttpRequestFactory(httpClient))
        );

        baseUrl = "http://localhost:" + port + "/auth/login";
        System.out.println("Base URL: " + baseUrl);
    }

    @Test
    @DisplayName("1. 로그인 실패: 올바르지 않은 아이디와 패스워드")
    void loginFailTest() {
        // given(테스트 조건 준비)
        LoginDTO.LoginRequest invalidRequest = new LoginDTO.LoginRequest();
        invalidRequest.setEmail("invalidEmail@gmail.com");
        invalidRequest.setPassword("invalidPassword");
        String expectedErrorMessage = "아이디 혹은 이메일이 유효하지 않습니다.";

        // when(테스트 액션 실행)
        ResponseEntity<LoginDTO.LoginResponse> response = restTemplate.postForEntity(
                baseUrl, invalidRequest, LoginDTO.LoginResponse.class);
        // then(테스트 결과 검증)
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getAccessToken()).isNull();
        assertThat(response.getBody().getName()).isNull();
        assertThat(response.getBody().getEmail()).isNull();
        assertThat(response.getBody().getLastLoginDate()).isNull();
        assertThat(response.getBody().getErrorMessage()).isEqualTo(expectedErrorMessage);
    }

    @Test
    @DisplayName("2. 로그인 성공: 올바른 아이디와 패스워드")
    void loginSuccessTest() {
        // given
        LoginDTO.LoginRequest validRequest = new LoginDTO.LoginRequest();
        validRequest.setEmail("cbkdevelop57@gmail.com");
        validRequest.setPassword("passwordTest1234!@");

        // when
        ResponseEntity<LoginDTO.LoginResponse> response = restTemplate.postForEntity(baseUrl, validRequest, LoginDTO.LoginResponse.class);
//        System.out.println("로그인 성공 응답: " + objectMapper.writeValueAsString(response.getBody()));

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getAccessToken()).isNotBlank();
        assertThat(response.getBody().getName()).isEqualTo("최병권");
        assertThat(response.getBody().getEmail()).isEqualTo("cbkdevelop57@gmail.com");
        assertThat(response.getBody().getLastLoginDate()).isNotNull();
        assertThat(response.getBody().getErrorMessage()).isNull();
    }

    @Test
    @DisplayName("3. 로그인 실패: 아이디가 누락된 경우")
    void loginMissingEmailTest() throws JsonProcessingException {
        // given(테스트 조건 준비)
        LoginDTO.LoginRequest didNotInputEmailValueRequest = new LoginDTO.LoginRequest();
        didNotInputEmailValueRequest.setPassword("passwordTest1234!@"); // 이메일 누락
        String expectedErrorMessage = "아이디와 비밀번호는 필수 입력값입니다.";

        // when(테스트 액션 실행)
        ResponseEntity<LoginDTO.LoginResponse> response =
                restTemplate.postForEntity(baseUrl, didNotInputEmailValueRequest, LoginDTO.LoginResponse.class);

        // JSON 응답 출력
//        System.out.println("이메일 누락 응답: " + objectMapper.writeValueAsString(response.getBody()));
        System.out.println("이메일 누락 응답: " + response.getBody());

        // then(테스트 결과 검증)
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getAccessToken()).isNull();
        assertThat(response.getBody().getName()).isNull();
        assertThat(response.getBody().getEmail()).isNull();
        assertThat(response.getBody().getLastLoginDate()).isNull();
        assertThat(response.getBody().getErrorMessage()).isEqualTo(expectedErrorMessage);
    }

    @Test
    @DisplayName("4. 로그인 실패: 비밀번호가 누락된 경우")
    void loginMissingPasswordTest() throws JsonProcessingException {
        // given(테스트 조건 준비)
        LoginDTO.LoginRequest didNotInputPasswordValueRequest = new LoginDTO.LoginRequest();
        didNotInputPasswordValueRequest.setEmail("cbkdevelop57@gmail.com"); // 비밀번호 누락
        String expectedErrorMessage = "아이디와 비밀번호는 필수 입력값입니다.";

        // when(테스트 액션 실행)
        ResponseEntity<LoginDTO.LoginResponse> response =
                restTemplate.postForEntity(baseUrl, didNotInputPasswordValueRequest, LoginDTO.LoginResponse.class);

        // JSON 응답 출력
        System.out.println("패스워드 누락 응답: " + response.toString()); // ResponseEntity 객체의 기본 toString() 출력
        System.out.println("패스워드 누락 응답: " + response.getBody());  // 실제 응답객체(LoginResponse)의 override된 toString() 출력

        // then(테스트 결과 검증)
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getAccessToken()).isNull();
        assertThat(response.getBody().getName()).isNull();
        assertThat(response.getBody().getEmail()).isNull();
        assertThat(response.getBody().getLastLoginDate()).isNull();
        assertThat(response.getBody().getErrorMessage()).isEqualTo(expectedErrorMessage);
    }
}