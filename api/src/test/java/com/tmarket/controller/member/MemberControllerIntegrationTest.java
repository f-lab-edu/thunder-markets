package com.tmarket.controller.member;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tmarket.model.conf.PropertyConfig;
import com.tmarket.model.member.LoginDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // 내장 Random Port 사용
public class MemberControllerIntegrationTest {

    @Autowired  // TestRestTemplate 사용
    private TestRestTemplate restTemplate;

    @Autowired // @Value 대신 @ConfigurationProperties 사용
    private PropertyConfig propertyConfig;

    // ResponseEntity객체가 @ResponseBody로 직렬화된 JSON 데이터를 반환하는 것과 달리
    // 테스트코드에서는 response 출력시 응답객체대신 객체의 메모리가 참조되고 있다.
    //  => 로그인 실패 응답: <401 UNAUTHORIZED Unauthorized,com.tmarket.model.member.LoginDTO$LoginResponse@1e029a04,[Set-Cookie:"JSESSIONID=EE53C4C1E0B1D27994B6559705080D4C; Path=/; HttpOnly", Content-Type:"application/json", Transfer-Encoding:"chunked", Date:"Sat, 11 Jan 2025 12:00:15 GMT", Keep-Alive:"timeout=60", Connection:"keep-alive"]>
    //  => 로그인 성공 응답: <200 OK OK,com.tmarket.model.member.LoginDTO$LoginResponse@70421a08,[Set-Cookie:"JSESSIONID=FDD34B6E2CC423C00662724412316A4B; Path=/; HttpOnly", Content-Type:"application/json", Transfer-Encoding:"chunked", Date:"Sat, 11 Jan 2025 12:17:13 GMT", Keep-Alive:"timeout=60", Connection:"keep-alive"]>
    // 이유는 TestRestTemplate은 테스트코드에서 ResponseEntity.getBody()를 호출하면, HTTP 요청의 응답을 객체(LoginResponse)로 역직렬화하기때문이다.
    // 이때 객체의 toString() 메서드가 오버라이드되지 않은 경우, 객체의 참조 값(com.tmarket.model.member.LoginDTO$LoginResponse@1e029a04)이 출력된다.
    // getBody()의 내용을 확인하려면 두 가지 방법이 있는데
    // LoginResponse객체의 toString()을 오버라이드하거나,
    // JSON 직렬화 라이브러리인 Jackson의 ObjectMapper를 사용해 response.getBody()로 반환된 객체를 JSON 문자열로 변환하여 출력할 수 있다.
    // 여기서는 ObjectMapper의 사용법을 익히기 위해 후자의 방식을 채택하였다.
    //  => 로그인 실패 응답: {"token":"아이디 혹은 이메일이 유효하지 않습니다.","name":null,"email":null,"lastDt":null}
    //  => 로그인 성공 응답: {"token":"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjYmtkZXZlbG9wNTdAZ21haWwuY29tIiwibmFtZSI6Iuy1nOuzkeq2jCIsImV4cCI6MTczNjY4NDI4NX0.F1mLoR2xfEirG9XNQ1tEZ-zNH1d5BL6K0DarYx8whb4","name":"최병권","email":"cbkdevelop57@gmail.com","lastDt":"2025-01-11T12:18:05.733+00:00"}
    // @Autowired // Jackson ObjectMapper 주입
//    private ObjectMapper objectMapper;
    // => 단순 로깅 전략으로 ObjectMapper는 너무 무겁다는 의견이 나와 toString으로 교체(250120)

    private String baseUrl;

    @BeforeEach // 테스트 초기화
    public void setup() {
        System.out.println("propertyConfig.getDefaultUrl() =" + propertyConfig.getAuthLogintUrl());
        baseUrl = propertyConfig.getAuthLogintUrl();
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
        ResponseEntity<LoginDTO.LoginResponse> response = restTemplate.postForEntity(baseUrl, invalidRequest, LoginDTO.LoginResponse.class);
        // 개인 테스트용 코드들은 지우는 것을 추천 (추후 테스트 코드에서는 삭제하기로 함) - 이 코드는 이에 대한 기록을 위해 보존
//        System.out.println("로그인 실패 응답: " + objectMapper.writeValueAsString(response.getBody()));

        // then(테스트 결과 검증)
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getToken()).isEqualTo(expectedErrorMessage);
        assertThat(response.getBody().getName()).isNull();
        assertThat(response.getBody().getEmail()).isNull();
        assertThat(response.getBody().getLastDt()).isNull();
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
        assertThat(response.getBody().getToken()).isNotBlank();
        assertThat(response.getBody().getName()).isEqualTo("최병권");
        assertThat(response.getBody().getEmail()).isEqualTo("cbkdevelop57@gmail.com");
        assertThat(response.getBody().getLastDt()).isNotNull();
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
        assertThat(response.getBody().getToken()).isEqualTo(expectedErrorMessage);
        assertThat(response.getBody().getName()).isNull();
        assertThat(response.getBody().getEmail()).isNull();
        assertThat(response.getBody().getLastDt()).isNull();
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
        assertThat(response.getBody().getToken()).isEqualTo(expectedErrorMessage);
        assertThat(response.getBody().getName()).isNull();
        assertThat(response.getBody().getEmail()).isNull();
        assertThat(response.getBody().getLastDt()).isNull();
    }
}