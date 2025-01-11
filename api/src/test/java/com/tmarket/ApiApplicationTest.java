package com.tmarket;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // 내장 Ramdom Port 사용
public class ApiApplicationTest {

    @Test // 모든 컨텍스트가 로드되어있는지 테스트해주는 기본 생성 어노테이션
    void contextLoads() {
        System.out.println("모든 컨텍스트 로드 완료");
    }
}
