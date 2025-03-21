package com.tmarket.controller.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmarket.model.conf.PropertyConfig;
import com.tmarket.model.member.LoginDTO;
import com.tmarket.model.product.ProductDTO;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProductControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    @Autowired
    private PropertyConfig propertyConfig;

    @Autowired
    private ResourceLoader resourceLoader;

    @LocalServerPort
    private int port;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        CloseableHttpClient httpClient = HttpClients.custom().build();
        this.restTemplate = new TestRestTemplate(
                restTemplateBuilder.requestFactory(() -> new HttpComponentsClientHttpRequestFactory(httpClient))
        );
        baseUrl = "http://localhost:" + port + "/products/register";
    }

    private HttpEntity<MultiValueMap<String, Object>> createRequestEntity(String token) throws IOException {
        // JSON 형태의 ProductDTO를 만들어서 HttpEntity로 포장
        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductName("MacBook Pro");
        productDTO.setProductTitle("맥북 프로 팝니다.");
        productDTO.setProductContent("최신형 MacBook Pro M3 맥북프로 팔아요 직거래만 합니다.");
        productDTO.setProductPrice(BigDecimal.valueOf(3299000.0));
        productDTO.setProductCategories(List.of("전자기기", "노트북", "애플"));
        productDTO.setPaymentOption("무통장입금");

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(productDTO);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization", token);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        // JSON part
        HttpHeaders jsonHeaders = new HttpHeaders();
        jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> jsonPart = new HttpEntity<>(json, jsonHeaders);
        body.add("product", jsonPart);

        // 이미지 파일 part
        Resource image = resourceLoader.getResource("classpath:스프링.png");
        body.add("images", image);

        return new HttpEntity<>(body, headers);
    }

    @Test
    @Order(1)
    @DisplayName("1. 유효하지 않은 토큰으로 상품 등록 실패")
    void registerProductWithInvalidToken() throws IOException {
        String invalidToken = "Bearer invalid_token";
        HttpEntity<MultiValueMap<String, Object>> request = createRequestEntity(invalidToken);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @Order(2)
    @DisplayName("2. 상품 등록 성공")
    void registerProductSuccessfully() throws IOException {
        // 유효한 토큰 획득 과정 (예: 로그인)
        String loginUrl = "http://localhost:" + port + propertyConfig.getAuthLogintUrl();

        LoginDTO.LoginRequest loginRequest = new LoginDTO.LoginRequest();
        loginRequest.setEmail("testuser@example.com");
        loginRequest.setPassword("password1234");

        ResponseEntity<LoginDTO.LoginResponse> loginResponse = restTemplate.postForEntity(
                loginUrl, loginRequest, LoginDTO.LoginResponse.class);

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        String validToken = "Bearer " + loginResponse.getBody().getAccessToken();

        HttpEntity<MultiValueMap<String, Object>> request = createRequestEntity(validToken);

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).contains("상품 등록에 성공하였습니다.");
    }
}
