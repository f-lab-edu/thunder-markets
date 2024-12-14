package com.api.unit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
        System.out.println("자 이제 시작이야 내꿈을~");
    }
}

// jar파일안에 main메소드가 없으면 bootJar를 구성할 수 없다.
// 어차피 web모듈이나 비즈니스 모듈은 main 메소드를 작성할 일이 없다. 서버 모듈에만 있으면 된다.
// 따라서 나머지 모듈에서는 bootJar를 꺼줘야 한다.