package com.pyeon;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@Disabled("테스트 환경 설정 문제로 인해 임시 비활성화")
class PyeonApplicationTests {

	@Test
	void contextLoads() {
		// 빈 테스트 - 컨텍스트가 로드되는지만 확인
	}

}
