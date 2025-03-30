package com.tmarket.model.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductImageDTO {

    private Long productImageIndex;
    private Date registDate;
    private Date modifyDate;
    private Date deleteDate;
    private String actFileName;
    private String actFileOriginName;
    private String imagePathName;
    private char deleteYesNo;

    // 사용자 정의 Constructor
//    public ProductImageDTO(ProductImage productImage) {
//        this.productImageIndex = productImage.getProductImageIndex();
//        this.registDate = productImage.getRegistDate();
//        this.modifyDate = productImage.getModifyDate();
//        this.deleteDate = productImage.getDeleteDate();
//        this.actFileName = productImage.getActFileName();
//        this.actFileOriginName = productImage.getActFileOriginName();
//        this.imagePathName = productImage.getImagePathName();
//        this.deleteYesNo = productImage.getDeleteYesNo();
//    }
}
