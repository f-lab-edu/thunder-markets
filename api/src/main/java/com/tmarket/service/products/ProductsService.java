package com.tmarket.service.products;

import com.tmarket.model.member.User;
import com.tmarket.model.product.*;
import com.tmarket.repository.member.UserRepository;
import com.tmarket.repository.product.ProductImageRepository;
import com.tmarket.repository.product.ProductsRepository;
import com.tmarket.service.util.ObjectStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductsService {

    private final ObjectStorageService objectStorageService;
    private final ProductsRepository productsRepository;
    private final ProductImageRepository productImageRepository;
    private final UserRepository userRepository;

    public ProductResponseDTO registerProduct(ProductDTO products, List<MultipartFile> images, String userId) {

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
        // 상품 저장
        productsRepository.save(product);

        // MultipartFile를 ProductImage 엔티티로 변환
        List<ProductImage> productImages = new ArrayList<>();
        for (MultipartFile image : images) {
            ProductImageDTO productImageDTO = objectStorageService.uploadFile(image); // 이미지 저장 후 URL 생성

            // ProductImage 객체 생성 후 productId 할당
            ProductImage productImage = ProductImage.builder()
                    .product(product)  // productId 할당
                    .registDate(productImageDTO.getRegistDate())
                    .modifyDate(productImageDTO.getModifyDate())
                    .deleteDate(productImageDTO.getDeleteDate())
                    .actFileName(productImageDTO.getActFileName())
                    .actFileOriginName(productImageDTO.getActFileOriginName())
                    .imagePathName(productImageDTO.getImagePathName())
                    .deleteYesNo(productImageDTO.getDeleteYesNo())
                    .build();

            productImages.add(productImage);
        }

        // 이미지 리스트 저장 및 엔티티를 DTO로 변환
        List<ProductImage> productImageEntity = productImageRepository.saveAll(productImages);
        List<ProductImageDTO> productImageDTO = new ArrayList<>();
        for (ProductImage productImage : productImageEntity) {
            productImageDTO.add(new ProductImageDTO(productImage));
        }

        // ProductResponseDTO 생성 및 반환
        return new ProductResponseDTO(products, productImageDTO);
    }
}
