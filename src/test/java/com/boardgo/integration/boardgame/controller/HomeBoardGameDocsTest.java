package com.boardgo.integration.boardgame.controller;

import static com.boardgo.common.constant.HeaderConstant.API_VERSION_HEADER;
import static io.restassured.RestAssured.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import com.boardgo.integration.init.TestBoardGameInitializer;
import com.boardgo.integration.support.RestDocsTestSupport;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.RequestPartsSnippet;

public class HomeBoardGameDocsTest extends RestDocsTestSupport {
    @Autowired TestBoardGameInitializer testBoardGameInitializer;

    @Test
    @DisplayName("메인홈 상황별 추천 보드게임")
    void 메인홈_상황별_추천_보드게임() {
        testBoardGameInitializer.generateBoardGameData();

        given(this.spec)
                .log()
                .all()
                .port(port)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(API_VERSION_HEADER, "1")
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .multiPart("situationType", "ALL")
                .filter(
                        document(
                                "get-home-situation",
                                getSituationRequestPartBodySnippet(),
                                getSituationResponseFieldsSnippet()))
                .when()
                .get("/home/situation")
                .then()
                .log()
                .ifError()
                .statusCode(HttpStatus.OK.value());
    }

    RequestPartsSnippet getSituationRequestPartBodySnippet() {
        return requestParts(
                partWithName("situationType").description("상황별 타입(TWO/THREE/MANY/ALL)"));
    }

    private ResponseFieldsSnippet getSituationResponseFieldsSnippet() {
        return responseFields(
                List.of(
                        fieldWithPath("[].title").type(JsonFieldType.STRING).description("보드게임명"),
                        fieldWithPath("[].thumbnail")
                                .type(JsonFieldType.STRING)
                                .description("보드게임 썸네일"),
                        fieldWithPath("[].minPlaytime")
                                .type(JsonFieldType.NUMBER)
                                .description("최소 시간"),
                        fieldWithPath("[].maxPlaytime")
                                .type(JsonFieldType.NUMBER)
                                .description("최대 시간"),
                        fieldWithPath("[].genres").type(JsonFieldType.ARRAY).description("장르 목록")));
    }
}
