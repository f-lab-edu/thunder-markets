package com.tmarket.model.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "property-file-config")
@Data
public class FileUploadConfig {
    private String fileDirectory;
    private String imageDirectory;
}
