package com.pyeon;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
class PyeonApplicationTests {

	@Test
	@Disabled("추후 테스트 코드 추가")
	void contextLoads() {
		// 빈 테스트 - 컨텍스트가 로드되는지만 확인
	}

}
