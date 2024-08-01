package com.boardgo.integration.support;

import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.test.context.support.WithMockUser;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import jakarta.transaction.Transactional;

@ExtendWith(RestDocumentationExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@AutoConfigureMockMvc
@WithMockUser
public abstract class RestDocsTestSupport {

	protected RequestSpecification spec;

	@LocalServerPort
	protected int port;

	@BeforeEach
	public void setUp(RestDocumentationContextProvider restDocumentation) {
		this.spec = new RequestSpecBuilder()
			.addFilter(documentationConfiguration(restDocumentation))
			.build();
	}

}

