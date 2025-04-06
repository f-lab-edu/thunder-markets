package com.tmarket.service.util;

import com.tmarket.model.product.ProductImage;
import com.tmarket.model.product.ThumbnailImageDTO;
import org.springframework.web.multipart.MultipartFile;

public interface ObjectStorageService {
    public ProductImage uploadFile(MultipartFile file);

    ThumbnailImageDTO uploadAndResizeImage(MultipartFile firstImage, int resizedWidth, int resizedHeight);
}
