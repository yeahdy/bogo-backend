package com.boardgo.integration.boardgame.controller;

import static com.boardgo.common.constant.HeaderConstant.*;
import static io.restassured.RestAssured.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.*;

import com.boardgo.domain.boardgame.controller.request.BoardGameCreateRequest;
import com.boardgo.integration.support.RestDocsTestSupport;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.specification.MultiPartSpecification;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

public class BoardGameDocsTest extends RestDocsTestSupport {
    @Test
    @DisplayName("사용자는 보드게임 데이터를 등록할 수 있다")
    void 사용자는_보드게임_데이터를_등록할_수_있다() throws JsonProcessingException {

        List<String> genres1 = Arrays.asList("Strategy", "Family");
        List<String> genres2 = Arrays.asList("Card", "Party");
        List<BoardGameCreateRequest> requestList =
                Arrays.asList(
                        new BoardGameCreateRequest("Game1", 2, 4, 30, 60, genres1),
                        new BoardGameCreateRequest("Game2", 1, 6, 20, 40, genres2));

        String requestJson = objectMapper.writeValueAsString(requestList);

        MultiPartSpecification imageSpec1 =
                new MultiPartSpecBuilder(new byte[] {1, 2, 3})
                        .fileName("image1.jpg")
                        .controlName("imageFileList")
                        .mimeType("image/jpeg")
                        .build();

        MultiPartSpecification imageSpec2 =
                new MultiPartSpecBuilder(new byte[] {4, 5, 6})
                        .fileName("image2.jpg")
                        .controlName("imageFileList")
                        .mimeType("image/jpeg")
                        .build();

        given(this.spec)
                .port(port)
                .log()
                .all()
                .accept(APPLICATION_JSON_VALUE)
                .contentType(MULTIPART_FORM_DATA_VALUE)
                .header(API_VERSION_HEADER, "1")
                .multiPart("boardGameCreateListRequest", requestJson, APPLICATION_JSON_VALUE)
                .multiPart(imageSpec1)
                .multiPart(imageSpec2)
                .filter(
                        document(
                                "boardgame-create",
                                requestParts(
                                        partWithName("boardGameCreateListRequest")
                                                .description("보드게임 데이터 리스트"),
                                        partWithName("imageFileList")
                                                .description("보드게임 썸네일 이미지 리스트")),
                                requestPartFields(
                                        "boardGameCreateListRequest",
                                        fieldWithPath("[].title")
                                                .type(JsonFieldType.STRING)
                                                .description("보드게임 이름"),
                                        fieldWithPath("[].minPeople")
                                                .type(JsonFieldType.NUMBER)
                                                .description("최소 인원 수"),
                                        fieldWithPath("[].maxPeople")
                                                .type(JsonFieldType.NUMBER)
                                                .description("최대 인원 수"),
                                        fieldWithPath("[].minPlaytime")
                                                .type(JsonFieldType.NUMBER)
                                                .description("최소 플레이타임"),
                                        fieldWithPath("[].maxPlaytime")
                                                .type(JsonFieldType.NUMBER)
                                                .description("최대 플레이타임"),
                                        fieldWithPath("[].genres[]")
                                                .type(JsonFieldType.ARRAY)
                                                .description(
                                                        "장르 String 타입으로 배열입니다! ex) [전략게임, 테마게임]"))))
                .when()
                .post("/boardgame")
                .then()
                .log()
                .all()
                .statusCode(201);
    }
}
