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

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductsService {

    private final ObjectStorageService objectStorageService;
    private final ProductsRepository productsRepository;
    private final ProductImageRepository productImageRepository;
    private final UserRepository userRepository;

    public ProductResponseDTO registerProduct(ProductDTO productDto, List<MultipartFile> images, String email) {

        // 판매자 정보 조회
        User sellerInfo = userRepository.findByEmail(email);
        if (sellerInfo == null) {
            throw new IllegalArgumentException("판매자를 찾을 수 없습니다.");
        }

        // ProductDTO를 Product 엔티티로 변환
        Product product = ProductDtoMapper.INSTANCE.productDTOtoProduct(productDto, sellerInfo);
        // Product product = new Product(productDto, sellerInfo);

        // 첫 번째 이미지 추출 후 thumbnailProductImage로 설정 (50x50 리사이징)
        if (!images.isEmpty()) {
            MultipartFile firstImage = images.get(0);
            ThumbnailImageDTO thumbnailInfo = objectStorageService.uploadAndResizeImage(firstImage, 500, 500);

            product.setThumbnailProductImage(thumbnailInfo.getFullPath()); // 전체 경로
            product.setThumbnailOriginName(thumbnailInfo.getOriginName()); // 원본 파일명
            product.setThumbnailFileName(thumbnailInfo.getFileName());     // 저장된 파일명
            product.setThumbnailFilePath(thumbnailInfo.getFilePath());     // 저장된 파일 경로
        }

        // 상품 저장
        product = productsRepository.save(product);
        ProductDTO productDtoResponse = ProductMapper.INSTANCE.productToProductDTO(product); // ProductDTO를 Product 엔티티로 변환

        // MultipartFile를 ProductImage 엔티티로 변환
        List<ProductImage> productImages = new ArrayList<>();
        for (MultipartFile image : images) {
            ProductImage productImage = objectStorageService.uploadFile(image); // 이미지 저장 후 DTO에 보관
            productImage.setProduct(product); // product 속성 설정
            productImages.add(productImage);
        }

        // 이미지 리스트 저장 및 엔티티를 DTO로 변환
        List<ProductImage> productImage = productImageRepository.saveAll(productImages);
//        List<ProductImageDTO> productImageDto = productImage.stream().map(ProductImageDTO::new).collect(Collectors.toList());
        List<ProductImageDTO> productImageDtoResponse = ProductImageMapper.INSTANCE.productImageToProductImageDTO(productImage);


        // ProductResponseDTO 생성 및 반환
        return new ProductResponseDTO(productDtoResponse, productImageDtoResponse);
    }

    public List<ProductResponseDTO> getProductList() {

        // 1. 전체 상품 조회
        List<Product> productList = productsRepository.findAll();

        // 2. 각 상품에 대해 이미지 조회 및 DTO 매핑
        return productList.stream()
                .map(product -> {
                    ProductDTO productDto = ProductMapper.INSTANCE.productToProductDTO(product);

                    List<ProductImage> productImages = productImageRepository.findAllByProduct(product);
                    List<ProductImageDTO> productImageDtoList = ProductImageMapper.INSTANCE.productImageToProductImageDTO(productImages);

                    return new ProductResponseDTO(productDto, productImageDtoList);
                })
                .toList();
    }
}
