package com.boardgo.integration.support;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.boardgo.common.exception.advice.CommonControllerAdvice;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

@ExtendWith(RestDocumentationExtension.class)
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@WithMockUser
public abstract class RestDocsTestSupport {

	protected MockMvc mockMvc;
	protected ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	void setUp(ApplicationContext context, RestDocumentationContextProvider provider) {
		this.mockMvc = MockMvcBuilders.standaloneSetup(initializeController())
			.setControllerAdvice(CommonControllerAdvice.class)
			.apply(documentationConfiguration(provider))
			.build();
	}

	protected abstract Object initializeController();
}

