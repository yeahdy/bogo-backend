package com.boardgo.integration.support;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(RestDocumentationExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@WithMockUser
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@ActiveProfiles("test")
public abstract class RestDocsTestSupport {

    protected RequestSpecification spec;

    @LocalServerPort protected int port;

    @BeforeEach
    public void setUp(RestDocumentationContextProvider restDocumentation) {
        this.spec =
                new RequestSpecBuilder()
                        .addFilter(
                                documentationConfiguration(restDocumentation)
                                        .operationPreprocessors()
                                        .withRequestDefaults(
                                                modifyUris().host("54.180.60.122").port(8080),
                                                prettyPrint())
                                        .withResponseDefaults(prettyPrint()))
                        .build();
    }
}
