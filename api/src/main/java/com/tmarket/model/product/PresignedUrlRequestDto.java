package com.tmarket.model.product;

import lombok.Data;

@Data
public class PresignedUrlRequestDto {
    private String fileName;
    private String contentType;
}
