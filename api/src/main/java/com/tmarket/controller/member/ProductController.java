package com.tmarket.controller.member;


import com.tmarket.model.product.PresignedUrlDto;
import com.tmarket.model.product.PresignedUrlRequestDto;
import com.tmarket.model.product.ProductDTO;
import com.tmarket.model.product.ProductResponseDTO;
import com.tmarket.service.products.ProductsService;
import com.tmarket.service.util.ObjectStorageServiceWithS3;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

    private final ObjectStorageServiceWithS3 objectStorageServiceWithS3;

    @PostMapping(value ="/register",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Map<String, Object>> registerProduct(
            @RequestPart(value = "product", required = true) ProductDTO products,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            Authentication authentication) {
        logger.debug("이미지 개수: " + (images != null ? images.size() : "null"));

        String email = authentication.getName();

        ProductResponseDTO response = productsService.registerProduct(products, images, email);
        return ResponseEntity.status(201).body(Map.of("message", "상품 등록에 성공하였습니다.", "data", response));
    }

    @GetMapping(value = "/list",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Map<String, Object>> getProductList() {
        List<ProductResponseDTO> productList = productsService.getProductList();
        return ResponseEntity.ok(Map.of("message", "상품 목록 조회에 성공하였습니다.", "data", productList));
    }

    @PostMapping("/presigned-url")
    public ResponseEntity<PresignedUrlDto> getPresignedUrl(@RequestBody PresignedUrlRequestDto request) {
        PresignedUrlDto url = objectStorageServiceWithS3.generatePresignedUrl(request.getFileName(), request.getContentType());
        return ResponseEntity.ok(url);
    }
}
