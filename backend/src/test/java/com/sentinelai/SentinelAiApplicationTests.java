package com.sentinelai;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class SentinelAiApplicationTests {

	@Test
	void contextLoadsOnJava25AndSpringBoot412() {
		assertThat(Runtime.version().feature()).isEqualTo(25);
		assertThat(SpringBootVersion.getVersion()).isEqualTo("4.1.2");
	}

}

