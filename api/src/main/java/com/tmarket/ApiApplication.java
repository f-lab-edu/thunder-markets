package com.tmarket;

import com.tmarket.model.conf.PropertyConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@EntityScan(basePackages = "com.tmarket.model")
@EnableJpaRepositories(basePackages = "com.tmarket.repository")
@EnableConfigurationProperties(PropertyConfig.class)
public class ApiApplication {
    private static final Logger logger = LoggerFactory.getLogger(ApiApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
        logger.info("========thunder-market 에 오신것을 환영합니다.========");
    }
}