package com.boardgo.integration.review.controller;

import static com.boardgo.common.constant.HeaderConstant.API_VERSION_HEADER;
import static com.boardgo.common.constant.HeaderConstant.AUTHORIZATION;
import static com.boardgo.integration.fixture.EvaluationTagFixture.getEvaluationTagEntity;
import static io.restassured.RestAssured.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import com.boardgo.domain.review.repository.EvaluationTagRepository;
import com.boardgo.integration.support.RestDocsTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class EvaluationTagDocs extends RestDocsTestSupport {

    @Autowired private EvaluationTagRepository evaluationTagRepository;

    @Test
    @DisplayName("평가태그 목록 조회하기")
    void 평가태그_목록_조회하기() {
        evaluationTagRepository.saveAll(getEvaluationTagEntity());

        given(this.spec)
                .log()
                .all()
                .port(port)
                .header(API_VERSION_HEADER, "1")
                .header(AUTHORIZATION, testAccessToken)
                .filter(document("get-evaluation-tags", getResponseFieldsSnippet()))
                .when()
                .get("/evaluation-tags")
                .then()
                .log()
                .ifError()
                .statusCode(HttpStatus.OK.value());
    }

    private ResponseFieldsSnippet getResponseFieldsSnippet() {
        return responseFields(
                fieldWithPath("positiveTags").type(JsonFieldType.ARRAY).description("긍정적 태그"),
                fieldWithPath("positiveTags[].evaluationTagId")
                        .type(JsonFieldType.NUMBER)
                        .description("평가 태그 고유 ID"),
                fieldWithPath("positiveTags[].tagPhrase")
                        .type(JsonFieldType.STRING)
                        .description("평가 태그 문구"),
                fieldWithPath("positiveTags[].evaluationType")
                        .type(JsonFieldType.STRING)
                        .description("평가 태그 타입(POSITIVE/NEGATIVE)"),
                fieldWithPath("negativeTags").type(JsonFieldType.ARRAY).description("부정적 태그"),
                fieldWithPath("negativeTags[].evaluationTagId")
                        .type(JsonFieldType.NUMBER)
                        .description("평가 태그 고유 ID"),
                fieldWithPath("negativeTags[].tagPhrase")
                        .type(JsonFieldType.STRING)
                        .description("평가 태그 문구"),
                fieldWithPath("negativeTags[].evaluationType")
                        .type(JsonFieldType.STRING)
                        .description("평가 태그 타입(POSITIVE/NEGATIVE)"));
    }
}
