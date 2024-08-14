package com.boardgo.integration.boardgame.controller;

import static com.boardgo.common.constant.HeaderConstant.*;
import static io.restassured.RestAssured.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.*;

import com.boardgo.domain.boardgame.controller.request.BoardGameCreateRequest;
import com.boardgo.integration.init.TestBoardGameInitializer;
import com.boardgo.integration.support.RestDocsTestSupport;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.builder.MultiPartSpecBuilder;
import io.restassured.specification.MultiPartSpecification;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

public class BoardGameDocsTest extends RestDocsTestSupport {

    @Autowired TestBoardGameInitializer testBoardGameInitializer;

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

    @Test
    @DisplayName("사용자는 보드게임을 검색할 수 있다")
    void 사용자는_보드게임을_검색할_수_있다() {
        // given
        testBoardGameInitializer.generateBoardGameData();
        given(this.spec)
                .log()
                .all()
                .port(port)
                .header(API_VERSION_HEADER, "1")
                .header(AUTHORIZATION, testAccessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .filter(
                        document(
                                "boardgame-list",
                                queryParameters(
                                        parameterWithName("count")
                                                .description(
                                                        "현재 검색 조건의 전체 개수(캐싱을 위함) 기존 요청에서 전체 개수를 가져올 수 있을 때 사용하시면 됩니다!")
                                                .optional(),
                                        parameterWithName("searchWord")
                                                .description("검색어 (제목으로 검색합니다!)")
                                                .optional(),
                                        parameterWithName("page")
                                                .description("페이지 위치 ex) 1, 2, 3 ...")
                                                .optional(),
                                        parameterWithName("size")
                                                .description("페이지 크기 (만약을 대비해서)")
                                                .optional()),
                                responseFields(
                                        fieldWithPath("content[]")
                                                .type(JsonFieldType.ARRAY)
                                                .description("content")
                                                .optional(),
                                        fieldWithPath("content[].id")
                                                .type(JsonFieldType.NUMBER)
                                                .description("보드게임 ID"),
                                        fieldWithPath("content[].title")
                                                .type(JsonFieldType.STRING)
                                                .description("보드게임 제목"),
                                        fieldWithPath("content[].thumbnail")
                                                .type(JsonFieldType.STRING)
                                                .description("보드게임 썸네일 URI"),
                                        fieldWithPath("content[].genres[]")
                                                .type(JsonFieldType.ARRAY)
                                                .description("genre 데이터 배열"),
                                        fieldWithPath("content[].genres[].id")
                                                .type(JsonFieldType.NUMBER)
                                                .description("genre id"),
                                        fieldWithPath("content[].genres[].genre")
                                                .type(JsonFieldType.STRING)
                                                .description("genre id"),
                                        fieldWithPath("totalElements")
                                                .type(JsonFieldType.NUMBER)
                                                .description("전체 개수 (요청 때 count에 넣어주시면 캐싱 됩니다..!)"),
                                        fieldWithPath("totalPages")
                                                .type(JsonFieldType.NUMBER)
                                                .description("전체 페이지 수"),
                                        fieldWithPath("number")
                                                .type(JsonFieldType.NUMBER)
                                                .description("현재 페이지"),
                                        fieldWithPath("size")
                                                .type(JsonFieldType.NUMBER)
                                                .description("한 페이지 당 개수"),
                                        fieldWithPath("sort.empty")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("정렬 조건이 비어있는지 여부"),
                                        fieldWithPath("sort.sorted")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("content가 정렬되어 있는지의 여부"),
                                        fieldWithPath("sort.unsorted")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("정렬이 안되어있는지의 여부"),
                                        fieldWithPath("first")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("가장 첫번째 페이지의 숫자"),
                                        fieldWithPath("last")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("가장 마지막 페이지의 숫자"),
                                        fieldWithPath("numberOfElements")
                                                .type(JsonFieldType.NUMBER)
                                                .description("현재 페이지에 있는 content.size()"),
                                        fieldWithPath("empty")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("페이지가 비어있는지 여부"),
                                        fieldWithPath("pageable.pageNumber")
                                                .type(JsonFieldType.NUMBER)
                                                .description("현재 페이지 번호"),
                                        fieldWithPath("pageable.pageSize")
                                                .type(JsonFieldType.NUMBER)
                                                .description("페이지 당 항목 수"),
                                        fieldWithPath("pageable.offset")
                                                .type(JsonFieldType.NUMBER)
                                                .description("현재 페이지의 시작 지점"),
                                        fieldWithPath("pageable.paged")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("페이지가 페이징 되었는지 여부"),
                                        fieldWithPath("pageable.unpaged")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("페이지가 페이징 되지 않았는지 여부"),
                                        fieldWithPath("pageable.sort.empty")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("정렬 조건이 비어있는지 여부"),
                                        fieldWithPath("pageable.sort.sorted")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("정렬이 되었는지 여부"),
                                        fieldWithPath("pageable.sort.unsorted")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("정렬되지 않았는지 여부"))))
                .when()
                .get("/boardgame")
                .then()
                .statusCode(HttpStatus.OK.value());
    }
}
