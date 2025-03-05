package com.tmarket.service.products;

import com.tmarket.model.member.User;
import com.tmarket.model.product.Products;
import com.tmarket.repository.member.UserRepository;
import com.tmarket.repository.product.ProductsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductsService {
    private final ProductsRepository productsRepository;
    private final UserRepository userRepository;


    public Products registerProduct(Products product, String userId) {
        // 판매자 정보 조회
        Optional<User> sellerInfo = userRepository.findById(userId);
        if (sellerInfo.isEmpty()) {
            throw new IllegalArgumentException("판매자를 찾을 수 없습니다.");
        }
        User seller = sellerInfo.get();

        // 상품 정보에 판매자 정보 설정
        product.setSeller(seller);

        // 상품 저장
        return productsRepository.save(product);
    }
}
