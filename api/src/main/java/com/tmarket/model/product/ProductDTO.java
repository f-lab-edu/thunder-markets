package com.tmarket.model.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {

    private Long productId;
    private String productName;
    private String productTitle;
    private String productContent;
    private BigDecimal productPrice;
    private List<String> productCategories;
    private String paymentOption;

    @Builder.Default
    private String thumbnailProductImage = null;

    @Builder.Default
    private String productStatus = "판매중";

    @Builder.Default
    private Boolean isActive = true;

    @Builder.Default
    private Date registDate = new Date();

    @Builder.Default
    private Date modifyDate = null;

    @Builder.Default
    private Date deleteDate = null;

    private Long sellerId;

    @Builder.Default
    private List<ProductImageDTO> productImages = new ArrayList<>();
}
