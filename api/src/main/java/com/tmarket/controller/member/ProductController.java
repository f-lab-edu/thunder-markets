package com.tmarket.controller.member;


import com.tmarket.model.product.Products;
import com.tmarket.service.products.ProductsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductsService productsService;

    @PostMapping("/register")
    public ResponseEntity<String> registerProduct(@RequestBody Products product,
                                             @RequestParam String userId) {
        productsService.registerProduct(product, userId);
        return ResponseEntity.status(201).body("상품 등록에 성공하였습니다");
    }
}
