package com.tmarket;

import com.tmarket.controller.member.MemberController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
@ComponentScan(basePackages= {"com.tmarket.*", "com.tmarket.controller.*", "com.tmarket.service.*"})
public class ApiApplication {
    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);

        logger.info("===================================================");
        logger.info("===================================================");
        logger.info("===================================================");
        logger.info("========thunder-market 에 오신것을 환영합니다.========");
        logger.info("===================================================");
        logger.info("===================================================");
        logger.info("===================================================");
    }
}