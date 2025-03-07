package com.tmarket.service.util;

import com.tmarket.model.conf.FileUploadConfig;
import com.tmarket.model.product.ProductImageDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ObjectStorageServiceWithNoCloud implements ObjectStorageService {


    private static final Logger logger = LoggerFactory.getLogger(ObjectStorageServiceWithNoCloud.class);

    private final FileUploadConfig fileUploadConfig;

    public final String fileDirectory;
    public final String imageDirectory;

    @Autowired
    public ObjectStorageServiceWithNoCloud(FileUploadConfig fileUploadConfig) {
        this.fileUploadConfig = fileUploadConfig;
        this.fileDirectory = fileUploadConfig.getFileDirectory();
        this.imageDirectory = fileUploadConfig.getImageDirectory();
    }

    @Override
    public ProductImageDTO uploadFile(MultipartFile file) {
        ProductImageDTO productImageDTO = new ProductImageDTO();

        String actFileOriginName = "";                  // 파일 원본명
        String actFileName = "";                        // UUID 파일명
        Path   filePath = Paths.get(fileDirectory);     // 파일 경로
        String fileExtension = "";                      // 파일 확장자

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");   // 날짜별 폴더 생성
        SimpleDateFormat dirFormat = new SimpleDateFormat("yyMMdd");			// 날짜별 파일 생성
        Date time = new Date();
        String timestamp = dateFormat.format(time);
        String dateDir = dirFormat.format(time);

        logger.debug("✔ File Upload Path: " + filePath.toString());

        // 기본 파일 저장 디렉토리
        if (!filePath.toFile().exists()) {
            logger.debug("기본 파일 저장 디렉토리 생성: " + filePath.toAbsolutePath().toString());
            try {
                Files.createDirectories(filePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // 날짜별 디렉토리 생성
        Path dateDirPath = filePath.resolve(dateDir);
        if (!Files.exists(dateDirPath)) {
            logger.debug("날짜별 디렉토리 생성: " + dateDirPath.toAbsolutePath().toString());
            try {
                Files.createDirectories(dateDirPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // 원본 파일명 추출
        actFileOriginName = file.getOriginalFilename();

        // 파일 확장자 추출
        if (actFileOriginName != null && actFileOriginName.lastIndexOf(".") > 0) {
            fileExtension = actFileOriginName.substring(actFileOriginName.lastIndexOf("."));
        }

        // 새 파일명 생성
        actFileName = UUID.randomUUID().toString() + fileExtension;

        // 최종 파일 경로
        Path uploadFilePath = Paths.get(dateDirPath.toString(), timestamp + "_" + actFileName);

        logger.debug("actFileOriginName=" + actFileOriginName);
        logger.debug("actFileName=" + timestamp + "_" + actFileName);
        logger.debug("filePath: " + uploadFilePath.toAbsolutePath().toString());

        try {
            file.transferTo(uploadFilePath); // 파일 저장
            logger.info("파일 저장 성공: {}", uploadFilePath.toAbsolutePath()); // 파일 저장 성공 시 로그 추가
        } catch (IllegalStateException e) {
            logger.error("파일 저장 실패 (IllegalStateException): {}", e.getMessage(), e);
        } catch (IOException e) {
            logger.error("파일 저장 실패 (IOException): {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error("파일 저장 실패 (Exception): {}", e.getMessage(), e);
        }
        productImageDTO.setProductImgageIndex(null); // Set appropriate value
        productImageDTO.setRegistDate(new Date());
        productImageDTO.setModifyDate(null);
        productImageDTO.setDeleteDate(null);
        productImageDTO.setActFileName(actFileName);
        productImageDTO.setActFileOriginName(actFileOriginName);
        productImageDTO.setImagePathName(uploadFilePath.toString());
        productImageDTO.setDeleteYesNo('N');    // 삭제여부 디폴트: N

        return productImageDTO;
    }
}
