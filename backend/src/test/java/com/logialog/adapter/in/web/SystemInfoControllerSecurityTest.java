package com.logialog.adapter.in.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SystemInfoControllerSecurityTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("GET /api/v1/system/info is permitted without authentication and returns valid JSON")
	void shouldPermitSystemInfoWithoutAuth() throws Exception {
		mockMvc.perform(get("/api/v1/system/info"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.springBootVersion").value("4.1.2"))
				.andExpect(jsonPath("$.virtualThreadsActive").value(true))
				.andExpect(jsonPath("$.javaVersion").value(org.hamcrest.Matchers.startsWith("25.")));
	}

	@Test
	@DisplayName("OPTIONS /api/v1/system/info responds to CORS preflight with configured allowed origin")
	void shouldHandleCorsPreflight() throws Exception {
		mockMvc.perform(options("/api/v1/system/info")
						.header("Origin", "http://localhost:5173")
						.header("Access-Control-Request-Method", "GET"))
				.andExpect(status().isOk())
				.andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"))
				.andExpect(header().string("Access-Control-Allow-Credentials", "true"));
	}

}
