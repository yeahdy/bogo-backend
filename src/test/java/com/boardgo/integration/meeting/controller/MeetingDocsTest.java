package com.boardgo.integration.meeting.controller;

import static com.boardgo.common.constant.HeaderConstant.*;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.*;

import com.boardgo.domain.meeting.controller.dto.MeetingCreateRequest;
import com.boardgo.integration.support.RestDocsTestSupport;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDateTime;
import java.util.Arrays;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

public class MeetingDocsTest extends RestDocsTestSupport {
    @Test
    @DisplayName("사용자는 모임을 만들 수 있다")
    void 사용자는_모임을_만들_수_있다() throws JsonProcessingException {
        MeetingCreateRequest request =
                new MeetingCreateRequest(
                        "Test Meeting",
                        "FREE",
                        10,
                        "title",
                        "Seoul",
                        "Gangnam",
                        "37.5665",
                        "126.9780",
                        LocalDateTime.now().plusDays(1),
                        Arrays.asList(1L, 2L),
                        Arrays.asList(1L, 2L));

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String requestJson = objectMapper.writeValueAsString(request);

        given(this.spec)
                .port(port)
                .log()
                .all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .header(API_VERSION_HEADER, "1")
                .header(AUTHORIZATION, testAccessToken)
                .multiPart("meetingCreateRequest", requestJson, MediaType.APPLICATION_JSON_VALUE)
                .multiPart("image", "test-image.jpg", "image/jpeg".getBytes())
                .filter(
                        document(
                                "meeting-create",
                                requestParts(
                                        partWithName("meetingCreateRequest")
                                                .description("Meeting creation details"),
                                        partWithName("image")
                                                .description("Meeting image file")
                                                .optional()),
                                requestPartFields(
                                        "meetingCreateRequest",
                                        fieldWithPath("content")
                                                .type(JsonFieldType.STRING)
                                                .description("모임 내용"),
                                        fieldWithPath("type")
                                                .type(JsonFieldType.STRING)
                                                .description("모임 타입 (FREE or ACCEPT)"),
                                        fieldWithPath("limitParticipant")
                                                .type(JsonFieldType.NUMBER)
                                                .description("최대 참가자 수"),
                                        fieldWithPath("title")
                                                .type(JsonFieldType.STRING)
                                                .description("모임 제목"),
                                        fieldWithPath("city")
                                                .type(JsonFieldType.STRING)
                                                .description("모임 도시"),
                                        fieldWithPath("county")
                                                .type(JsonFieldType.STRING)
                                                .description("모임 구(ex. 강남구, 성동구)"),
                                        fieldWithPath("latitude")
                                                .type(JsonFieldType.STRING)
                                                .description("모임 장소의 위도"),
                                        fieldWithPath("longitude")
                                                .type(JsonFieldType.STRING)
                                                .description("모임 장소의 경도"),
                                        fieldWithPath("meetingDatetime")
                                                .type(JsonFieldType.STRING)
                                                .description("모임 시간"),
                                        fieldWithPath("boardGameIdList")
                                                .type(JsonFieldType.ARRAY)
                                                .description(
                                                        "보드게임 id 리스트(배열) / 개발 서버에서는 더미 데이터 [1 ~ 10]까지 존재"),
                                        fieldWithPath("genreIdList")
                                                .type(JsonFieldType.ARRAY)
                                                .description(
                                                        "보드게임 장르 id 리스트(배열) / 개발 서버에서는 더미 데이터 [1 ~ 10]까지 존재")),
                                responseHeaders(
                                        headerWithName("Location")
                                                .description("만들어진 모임 URI")
                                                .optional())))
                .when()
                .post("/meeting")
                .then()
                .log()
                .all()
                .statusCode(201)
                .header("Location", matchesPattern("/meeting/\\d+"));
    }
}
