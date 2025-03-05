package com.tmarket.model.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component // 빈으로 등록
@ConfigurationProperties(prefix = "property-test-config")  // prefix 설정 from application.yml
@Data   // @Setter 필요
public class PropertyConfig {
    private String authLogintUrl;
    private String port;
}
