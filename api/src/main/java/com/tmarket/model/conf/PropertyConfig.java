package com.tmarket.model.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component // 빈으로 등록
@ConfigurationProperties(prefix = "property-test-config")  // prefix 설정 from application.yml
// propertyConfig시 Prefix must be in canonical form에러 발생 : 네이밍 표준 형식을 사용해야 한다.
// ex) myApp (X) → my-app (O), myCommonProperties (X) → my-common-properties (O)

//@Component // 빈으로 등록 -> @EnableConfigurationProperties(PropertyConfig.class) 으로 대체
@Data   // @Setter 필요
public class PropertyConfig {
    private String authLogintUrl;
    private String port;
}
