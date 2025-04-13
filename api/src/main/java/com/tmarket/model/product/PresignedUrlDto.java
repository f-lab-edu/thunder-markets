package com.tmarket.model.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PresignedUrlDto {
    private String presignedUrl;
    private String objectKey;
}
