package com.tmarket.service.util;

import com.tmarket.model.product.ProductImageDTO;
import org.springframework.web.multipart.MultipartFile;

public interface ObjectStorageService {
    public ProductImageDTO uploadFile(MultipartFile file);
}
