package com.tmarket.service.util;

import com.tmarket.model.conf.FileUploadConfig;
import com.tmarket.model.product.ProductImage;
import com.tmarket.model.product.ThumbnailImageDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
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
    public ProductImage uploadFile(MultipartFile file) {
        ProductImage productImage = new ProductImage();

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
        productImage.setProductImgageIndex(null);
        productImage.setRegistDate(new Date());
        productImage.setModifyDate(null);
        productImage.setDeleteDate(null);
        productImage.setActFileName(actFileName);
        productImage.setActFileOriginName(actFileOriginName);
        productImage.setImagePathName(uploadFilePath.toString());
        productImage.setDeleteYesNo('N');    // 삭제여부 디폴트: N

        return productImage;
    }

    @Override
    public ThumbnailImageDTO uploadAndResizeImage(MultipartFile firstImage, int resizedWidth, int resizedHeight) {

        String actFileOriginName = "";                  // 파일 원본명
        String actFileName = "";                        // UUID 파일명
        Path   filePath = Paths.get(fileDirectory);     // 파일 경로
        String fileExtension = "";                      // 파일 확장자

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date time = new Date();
        String timestamp = dateFormat.format(time);

        logger.debug("✔ 파일 저장 경로: " + filePath.toString());

        actFileOriginName = firstImage.getOriginalFilename();   // 업로드 된 이미지 중 최초의 이미지 추출
        if (actFileOriginName != null && actFileOriginName.lastIndexOf(".") > 0) {
            fileExtension = actFileOriginName.substring(actFileOriginName.lastIndexOf("."));
        }
        actFileName = "thumb_" + timestamp.substring(0, 8) + "_" + UUID.randomUUID().toString().substring(0, 8) + fileExtension;

        Path uploadFilePath = Paths.get(filePath.toString(), actFileName);

        try {
            // 리사이징 로직
            BufferedImage originalImage = ImageIO.read(firstImage.getInputStream()); // 원본 이미지를 읽어들임

            int originalWidth = originalImage.getWidth();   // 원본 비율에 맞게 리사이징
            int originalHeight = originalImage.getHeight();
            double aspectRatio = (double) originalWidth / originalHeight;

            int newWidth = resizedWidth;
            int newHeight = (int) (resizedWidth / aspectRatio);//  = int newHeight = (int) ((resizedWidth * originalHeight) / originalWidth);
            if (newHeight > resizedHeight) {    // 리사이징 크기가 원본보다 크면 원본사이즈로 강제 조정
                newHeight = resizedHeight;
                newWidth = (int) (resizedHeight * aspectRatio);
            }

            // 고품질 리사이징 적용 (SCALE_SMOOTH)
            Image resizedTempImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH); // 원본 이미지를 안전한 RGB타입으로 변환(0 또는 TYPE_CUSTOM 방지)
            BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, originalImage.getType());  // 리사이징된 이미지를 위한 BufferedImage 객체 생성
            Graphics2D g = resizedImage.createGraphics(); // Graphics2D 객체 생성
            g.drawImage(resizedTempImage, 0, 0, null); // 원본 이미지를 리사이징된 이미지에 그림
            g.dispose(); // Graphics2D 객체 자원 해제

            // 리사이징된 이미지 파일 저장
            if ("jpg".equalsIgnoreCase(fileExtension) || "jpeg".equalsIgnoreCase(fileExtension)) {   // JPEG 품질 설정
                File outputFile = uploadFilePath.toFile();
                ImageWriter jpgWriter = ImageIO.getImageWritersByFormatName("jpg").next();
                ImageWriteParam jpgWriteParam = jpgWriter.getDefaultWriteParam();
                jpgWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                jpgWriteParam.setCompressionQuality(0.8f); // 0.0 (저품질) ~ 1.0 (고품질) 범위

                FileImageOutputStream outputStream = new FileImageOutputStream(outputFile);
                jpgWriter.setOutput(outputStream);
                jpgWriter.write(null, new IIOImage(resizedImage, null, null), jpgWriteParam);
                outputStream.close();
                jpgWriter.dispose();
            } else {   // 그 외 다른 포맷(PNG, GIF 등)은 일반 저장
                ImageIO.write(resizedImage, fileExtension.replace(".", ""), uploadFilePath.toFile());
            }

            logger.info("리사이징된 파일 저장 성공: {}", uploadFilePath.toAbsolutePath()); // 저장 성공 로그 출력
        } catch (IOException e) {
            logger.error("리사이징된 파일 저장 실패: {}", e.getMessage(), e); // 저장 실패 로그 출력
            throw new RuntimeException(e); // 예외 발생 시 RuntimeException 던짐
        }

        return new ThumbnailImageDTO(
                actFileOriginName,
                actFileName,
                filePath.toString(),
                uploadFilePath.toString()
        );
    }
}
