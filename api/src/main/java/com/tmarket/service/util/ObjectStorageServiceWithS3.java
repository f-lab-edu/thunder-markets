package com.tmarket.service.util;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.tmarket.model.product.PresignedUrlDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ObjectStorageServiceWithS3 {

    private final AmazonS3 amazonS3;

    @Value("${ncp.s3.bucket}")
    private String bucketName;

    public PresignedUrlDto generatePresignedUrl(String originalFileName, String contentType) {
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String objectKey = "products/" + UUID.randomUUID() + fileExtension;

        Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 30); // 30분 유효

        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, objectKey)
                .withMethod(HttpMethod.PUT)
                .withExpiration(expiration);
        request.setContentType(contentType);

        URL url = amazonS3.generatePresignedUrl(request);
        return new PresignedUrlDto(url.toString(), objectKey);
    }
}
