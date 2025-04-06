package com.tmarket.model.product;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThumbnailImageDTO {
    private String originName;  // 원본 파일명
    private String fileName;    // 썸네일 파일명 (UUID 포함)
    private String filePath;    // 썸네일 경로
    private String fullPath;    // 썸네일 전체 경로 (filePath + fileName)
}
