package com.tmarket.service.products;

import com.tmarket.model.member.User;
import com.tmarket.model.product.*;
import com.tmarket.repository.member.UserRepository;
import com.tmarket.repository.product.ProductImageRepository;
import com.tmarket.repository.product.ProductsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductsService {
    private final ProductsRepository productsRepository;
    private final ProductImageRepository productImageRepository;
    private final UserRepository userRepository;


    public ProductResponseDTO registerProduct(ProductDTO products, List<ProductImageDTO> images, String userId) {
        // 판매자 정보 조회
        Optional<User> sellerInfo = userRepository.findById(userId);
        if (sellerInfo.isEmpty()) {
            throw new IllegalArgumentException("판매자를 찾을 수 없습니다.");
        }
        User seller = sellerInfo.get();

        // 상품 정보에 판매자 정보 설정
        products.setSellerId(seller.getUserId());

        // ProductDTO를 Products 엔티티로 변환
        Products product = new Products(products, seller);

        // ProductImageDTO를 ProductImage 엔티티로 변환
        //ProductImage productImage = new ProductImage(images, product);
        // ProductImageDTO 리스트를 ProductImage 엔티티 리스트로 변환
        List<ProductImage> productImages = images.stream()
                .map(imageDTO -> new ProductImage(imageDTO, product))
                .collect(Collectors.toList());

        // 상품 저장
        productsRepository.save(product);
        // 이미지 리스트 저장
        productImageRepository.saveAll(productImages);
//        productImageRepository.save(productImage);

        // ProductResponseDTO 생성 및 반환
        ProductResponseDTO response = new ProductResponseDTO(products, images);
        return response;
    }
}
