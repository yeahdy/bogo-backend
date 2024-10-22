package com.boardgo.integration.termsconditions.controller;

import static com.boardgo.common.constant.HeaderConstant.API_VERSION_HEADER;
import static com.boardgo.integration.fixture.TermsConditionsFixture.getTermsConditionsList;
import static io.restassured.RestAssured.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import com.boardgo.domain.termsconditions.repository.TermsConditionsRepository;
import com.boardgo.integration.support.RestDocsTestSupport;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.QueryParametersSnippet;

public class TermsConditionsRestDocs extends RestDocsTestSupport {
    @Autowired private TermsConditionsRepository termsConditionsRepository;

    @BeforeEach
    void init() {
        termsConditionsRepository.saveAll(getTermsConditionsList());
    }

    @Test
    @DisplayName("약관동의 항목 목록을 조회한다")
    void 약관동의_항목_목록을_조회한다() {
        given(this.spec)
                .port(port)
                .log()
                .all()
                .header(API_VERSION_HEADER, "1")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .filter(
                        document(
                                "get-terms-conditions",
                                getTermsConditionsQueryParametersSnippet(),
                                getTermsConditionsResponseFieldsSnippet()))
                .queryParam("required", "TRUE,FALSE")
                .when()
                .get("/terms-conditions")
                .then()
                .statusCode(HttpStatus.OK.value())
                .log()
                .ifError()
                .extract();
    }

    private QueryParametersSnippet getTermsConditionsQueryParametersSnippet() {
        return queryParameters(
                parameterWithName("required").description("TRUE 또는 FALSE 또는 TRUE,FALSE"));
    }

    private ResponseFieldsSnippet getTermsConditionsResponseFieldsSnippet() {
        return responseFields(
                List.of(
                        fieldWithPath("[].type").type(JsonFieldType.STRING).description("약관동의 타입"),
                        fieldWithPath("[].title").type(JsonFieldType.STRING).description("약관동의 제목"),
                        fieldWithPath("[].content")
                                .type(JsonFieldType.STRING)
                                .optional()
                                .description("약관동의 내용"),
                        fieldWithPath("[].required")
                                .type(JsonFieldType.BOOLEAN)
                                .description("필수 여부")));
    }
}
