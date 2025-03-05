package com.tmarket.model.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
@Builder
public class ProductImageDTO {

    private Long productImgageIndex;
    private Date registDate;
    private Date modifyDate;
    private Date deleteDate;
    private String actFileName;
    private String actFileOriginName;
    private String imagePathName;
    private char deleteYesNo;
}
