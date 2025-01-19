package com.tmarket;

import org.junit.jupiter.api.Test;

// 통합 테스트 코드끼리 연동이 되는지 확인하기 위해 MemberControllerIntegrationTest에서
// @SpringBootTest을 지우고 ApiApplicationTest의 @SpringBootTest에 영향을 받는지 테스트함
// 통합테스트 코드끼리는 서로 영향을 줄 수 없다는 것을 알게되었음
// 이 클래스에서 @SpringBootTest 어노테이션은 어디상 필요없으나 기록용도로 보존함
// @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApiApplicationTest {

    @Test // 모든 컨텍스트가 로드되어있는지 테스트해주는 기본 생성 어노테이션
    void contextLoads() {
        System.out.println("모든 컨텍스트 로드 완료");
    }
}
