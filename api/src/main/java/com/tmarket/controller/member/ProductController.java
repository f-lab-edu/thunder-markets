package com.tmarket.controller.member;


import com.tmarket.model.product.ProductDTO;
import com.tmarket.model.product.ProductResponseDTO;
import com.tmarket.service.authentication.AuthenticationService;
import com.tmarket.service.products.ProductsService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    private final ProductsService productsService;
    private final AuthenticationService authentication;

    @PostMapping(value ="/register",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Map<String, Object>> registerProduct(
            @RequestPart(value = "product", required = true) ProductDTO products,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestHeader("Authorization") String token) {
        logger.debug("이미지 개수: " + (images != null ? images.size() : "null"));

        String email = authentication.validateTokenAndGetUserId(token);

        ProductResponseDTO response = productsService.registerProduct(products, images, email);
        return ResponseEntity.status(201).body(Map.of("message", "상품 등록에 성공하였습니다.", "data", response));
    }
}
