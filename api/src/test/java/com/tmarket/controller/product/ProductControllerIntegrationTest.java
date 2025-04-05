package com.tmarket.controller.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmarket.model.product.ProductResponseDTO;
import com.tmarket.service.authentication.AuthenticationIntegrationTestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductControllerIntegrationTest extends AuthenticationIntegrationTestUtil {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private ObjectMapper objectMapper;

    @LocalServerPort
    private int port;

    private String productRegistUrl;
    private HttpEntity<String> productPart;
    private Resource imageResource;

    @BeforeEach
    void setUp() throws FileNotFoundException {
        productRegistUrl = "http://localhost:" + port + "/products/register";
        System.out.println("RegisterUrl: " + productRegistUrl);

        // JSON 생성 (ObjectMapper 없이)
        String jsonProduct = """
                {
                    "productName": "MacBook Pro",
                    "productTitle": "맥북 프로 팝니다.",
                    "productContent": "최신형 MacBook Pro M3 맥북프로 팔아요 직거래만 합니다.",
                    "productPrice": 3299000.0,
                    "productCategories": ["전자기기", "노트북", "애플"],
                    "paymentOption": "무통장입금"
                }
                """;

        HttpHeaders jsonHeaders = new HttpHeaders();
        jsonHeaders.setContentType(MediaType.APPLICATION_JSON);
        productPart = new HttpEntity<>(jsonProduct, jsonHeaders);

        imageResource = resourceLoader.getResource("classpath:스프링.png");
        if (!imageResource.exists()) {
            throw new FileNotFoundException("테스트 이미지 '스프링.png'가 존재하지 않습니다.");
        }
    }

    @Test
    @DisplayName("1. 상품 등록 성공")
    void registerProductSuccessfully() throws IOException {
        // given(multipart body 구성) (구성)
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("product", productPart);
        body.add("images", imageResource);

        HttpHeaders headers = getAuthHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // when (실행)
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                productRegistUrl,
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<>() {}
        );
        // ParameterizedTypeReference : 제네릭 타입을 갖는 객체의 타입 정보를 보존(제네릭 타입을 명확히 지정)하여
        // Map<String, Object>가 Map으로 인식되는 걸 방지
        // TestRestTemplate에서는 postForEntity() 대신 exchange() 메서드를 사용해야 함.
        System.out.println("상품 등록 응답: " + objectMapper.writeValueAsString(response.getBody()));

        // then (검증)
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsEntry("message", "상품 등록에 성공하였습니다.");

        // Object -> ProductResponseDTO 변환
        Object data = response.getBody().get("data");
        ProductResponseDTO productResponseDTO = objectMapper.convertValue(data, ProductResponseDTO.class);

        assertThat(productResponseDTO.getProductDTO()).isNotNull();
        assertThat(productResponseDTO.getProductDTO().getProductName()).isEqualTo("MacBook Pro");
        assertThat(productResponseDTO.getProductImageDTO()).isNotEmpty();
        assertThat(productResponseDTO.getProductImageDTO().get(0).getActFileOriginName()).isEqualTo("스프링.png");
    }

}
