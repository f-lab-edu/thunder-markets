package com.tmarket.model.product;

import com.tmarket.model.member.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "products")
public class Products {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;  // 상품명

    @Column(name = "product_title", nullable = false, length = 200)
    private String productTitle;  // 상품 제목

    @Column(name = "product_content", nullable = false, columnDefinition = "TEXT")
    private String productContent;  // 상품 설명 (TEXT 타입)

    @Column(name = "product_price", nullable = false)
    private BigDecimal productPrice;  // 상품 가격

    @ElementCollection
    @CollectionTable(name = "product_categories", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "category_name")
    private List<String> productCategories;  // 카테고리 리스트

    @Column(name = "payment_option", nullable = false, length = 50)
    private String paymentOption;  // 결제 옵션

    @Column(name = "thumb_product_img", nullable = false, length = 255)
    private String thumbnailProductImage;  // 대표 이미지 URL

    @Column(name = "product_stts", nullable = false, length = 20)
    private String productStatus;  // 상품 상태 (예: 판매 중, 품절 등)

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;  // 활성화 여부

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "reg_dt", nullable = false, updatable = false)
    private Date registDate;  // 등록 날짜

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "mod_dt")
    private Date modifyDate;  // 수정 날짜

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "del_dt")
    private Date deleteDate;  // 삭제 날짜

    // seller_id -> User 엔티티의 userId 참조 (ManyToOne)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller; // 판매자 ID (User 테이블과 FK 관계)

    // 상품 이미지 리스트 (1:N 관계)
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> productImages;

    // 엔티티 생성 전 실행되는 메서드
    @PrePersist
    protected void onCreate() {
        this.registDate = new Date();
        this.modifyDate = new Date();
    }

    // 엔티티 수정 전 실행되는 메서드
    @PreUpdate
    protected void onUpdate() {
        this.modifyDate = new Date();
    }


    public Products(ProductDTO productDTO, User seller) {
        this.productName = productDTO.getProductName();
        this.productTitle = productDTO.getProductTitle();
        this.productContent = productDTO.getProductContent();
        this.productPrice = productDTO.getProductPrice();
        this.productCategories = productDTO.getProductCategories();
        this.paymentOption = productDTO.getPaymentOption();
        this.thumbnailProductImage = productDTO.getThumbnailProductImage();
        this.productStatus = productDTO.getProductStatus();
        this.isActive = productDTO.getIsActive();
        this.registDate = productDTO.getRegistDate();
        this.modifyDate = productDTO.getModifyDate();
        this.deleteDate = productDTO.getDeleteDate();
        this.seller = seller;
        this.productImages = productDTO.getProductImages() != null ?
                productDTO.getProductImages().stream()
                        .map(imageDTO -> new ProductImage(imageDTO, this))
                        .collect(Collectors.toList()) :
                new ArrayList<>();
    }
}
