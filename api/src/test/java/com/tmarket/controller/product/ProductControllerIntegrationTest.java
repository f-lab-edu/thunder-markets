package com.tmarket.controller.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmarket.model.product.ProductDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProductControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    @Autowired
    private ObjectMapper objectMapper;

    @LocalServerPort
    private int port;

    private String baseUrl;

    @BeforeEach
    public void setup() {
        baseUrl = "http://localhost:" + port + "/products/register";
        System.out.println("Base URL: " + baseUrl);
    }

    @Test
    @DisplayName("상품 등록 실패: 유효하지 않은 토큰")
    void registerProductFailTest() throws Exception {
        // given
        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductName("MacBook Pro");
        productDTO.setProductTitle("맥북 프로 팝니다.");
        productDTO.setProductContent("최신형 MacBook Pro M3 맥북프로 팔아요 직거래만 합니다.");
        productDTO.setProductPrice(BigDecimal.valueOf(3299000.0));
        productDTO.setProductCategories(List.of("전자기기", "노트북", "애플"));
        productDTO.setPaymentOption("무통장입금");

        MockMultipartFile productPart = new MockMultipartFile(
                "product", "product.json",
                "application/json",
                objectMapper.writeValueAsBytes(productDTO)
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization", "InvalidToken");

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("product", productPart.getResource());

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // when
        ResponseEntity<Map> response = restTemplate.exchange(
                baseUrl, HttpMethod.POST, requestEntity, Map.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).containsEntry("message", "토큰이 유효하지 않습니다.");
    }

    @Test
    @DisplayName("상품 등록 성공: 올바른 데이터 입력")
    void registerProductSuccessTest() throws Exception {
        // given (테스트 요청 데이터 준비)
        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductName("MacBook Pro");
        productDTO.setProductTitle("맥북 프로 팝니다.");
        productDTO.setProductContent("최신형 MacBook Pro M3 맥북프로 팔아요 직거래만 합니다.");
        productDTO.setProductPrice(BigDecimal.valueOf(3299000.0));
        productDTO.setProductCategories(List.of("전자기기", "노트북", "애플"));
        productDTO.setPaymentOption("무통장입금");

        // JSON으로 변환한 `product` 데이터를 `MockMultipartFile`로 생성
        MockMultipartFile productPart = new MockMultipartFile(
                "product", "product.json",
                "application/json",
                objectMapper.writeValueAsBytes(productDTO)
        );

        // 테스트용 이미지 파일 로드
        File imageFile = new File("src/test/resources/스프링.png");
        MockMultipartFile imagePart = new MockMultipartFile(
                "images", imageFile.getName(),
                "image/png", new FileInputStream(imageFile)
        );

        // HTTP 요청 헤더 설정 (multipart/form-data)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("Authorization", "ValidToken"); // 테스트용 유효한 토큰

        // FormDataBody 생성
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("product", productPart);
        body.add("images", imagePart);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // when
        ResponseEntity<Map> response = restTemplate.exchange(
                baseUrl, HttpMethod.POST, requestEntity, Map.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).containsEntry("message", "상품 등록에 성공하였습니다.");
        assertThat(response.getBody().get("data")).isNotNull();
    }
}
