package com.logialog;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class LogiaLogApplicationTests {

	@Autowired
	private Environment environment;

	@Test
	@DisplayName("Context loads successfully on Java 25 LTS and Spring Boot 4.1.2 with Virtual Threads")
	void contextLoadsOnJava25AndSpringBoot412() {
		assertThat(Runtime.version().feature()).isEqualTo(25);
		assertThat(SpringBootVersion.getVersion()).isEqualTo("4.1.2");
		assertThat(environment.getProperty("spring.threads.virtual.enabled", Boolean.class)).isTrue();
	}

}

