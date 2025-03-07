package com.tmarket.model.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ProductDTO {

    private Long productId;
    private String productName;
    private String productTitle;
    private String productContent;
    private Double productPrice;
    private List<String> productCategories;
    private String paymentOption;
    private String thumbnailProductImage;
    private String productStatus;
    private Boolean isActive;
    private Date registDate;
    private Date modifyDate;
    private Date deleteDate;
    private Long sellerId;
    private List<ProductImageDTO> productImages;

}
