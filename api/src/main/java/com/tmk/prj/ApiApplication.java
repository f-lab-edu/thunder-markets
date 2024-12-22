package com.tmk.prj;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages= {"com.tmk.prj", "com.ctrl.*"})
@EntityScan("com.model.member")
//@EnableJpaRepositories("dao.*")
public class ApiApplication {
    private static final Logger logger = Logger.getLogger(ApiApplication.class.getName());

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
        logger.log(Level.INFO, "========thunder-market 실행========");
    }
}
