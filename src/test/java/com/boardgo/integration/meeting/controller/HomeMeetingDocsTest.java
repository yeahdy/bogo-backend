package com.boardgo.integration.meeting.controller;

import static com.boardgo.common.constant.HeaderConstant.API_VERSION_HEADER;
import static com.boardgo.integration.data.MeetingData.getMeetingEntityData;
import static io.restassured.RestAssured.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import com.boardgo.domain.meeting.repository.MeetingRepository;
import com.boardgo.integration.support.RestDocsTestSupport;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class HomeMeetingDocsTest extends RestDocsTestSupport {
    @Autowired MeetingRepository meetingRepository;

    @Test
    @DisplayName("메인홈 마감임박 모임 조회")
    void 메인홈_마감임박_모임_조회() {
        Long userId = 1L;
        meetingRepository.save(
                getMeetingEntityData(userId)
                        .meetingDatetime(LocalDateTime.now().plusHours(1))
                        .build());
        meetingRepository.save(
                getMeetingEntityData(userId)
                        .meetingDatetime(LocalDateTime.now().plusDays(1))
                        .build());
        meetingRepository.save(
                getMeetingEntityData(userId)
                        .meetingDatetime(LocalDateTime.now().plusDays(1))
                        .build());
        meetingRepository.save(
                getMeetingEntityData(userId)
                        .meetingDatetime(LocalDateTime.now().plusDays(2))
                        .build());
        meetingRepository.save(
                getMeetingEntityData(userId)
                        .meetingDatetime(LocalDateTime.now().plusDays(2))
                        .build());
        meetingRepository.save(
                getMeetingEntityData(userId)
                        .meetingDatetime(LocalDateTime.now().plusMinutes(59))
                        .build());
        meetingRepository.save(
                getMeetingEntityData(userId)
                        .meetingDatetime(LocalDateTime.now().plusHours(1))
                        .build());
        meetingRepository.save(
                getMeetingEntityData(userId)
                        .meetingDatetime(LocalDateTime.now().plusDays(1))
                        .build());
        meetingRepository.save(
                getMeetingEntityData(userId)
                        .meetingDatetime(LocalDateTime.now().plusDays(2))
                        .build());
        meetingRepository.save(
                getMeetingEntityData(userId)
                        .meetingDatetime(LocalDateTime.now().plusDays(2))
                        .build());

        given(this.spec)
                .log()
                .all()
                .port(port)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(API_VERSION_HEADER, "1")
                .filter(document("get-home-deadline", getMeetingDeadlineResponseFieldsSnippet()))
                .when()
                .get("/home/deadline")
                .then()
                .log()
                .ifError()
                .statusCode(HttpStatus.OK.value());
    }

    private ResponseFieldsSnippet getMeetingDeadlineResponseFieldsSnippet() {
        return responseFields(
                List.of(
                        fieldWithPath("[].meetingId")
                                .type(JsonFieldType.NUMBER)
                                .description("모임 고유 id"),
                        fieldWithPath("[].thumbnail")
                                .type(JsonFieldType.STRING)
                                .description("보드게임 썸네일"),
                        fieldWithPath("[].title").type(JsonFieldType.STRING).description("보드게임 제목"),
                        fieldWithPath("[].city").type(JsonFieldType.STRING).description("도시"),
                        fieldWithPath("[].county")
                                .type(JsonFieldType.STRING)
                                .description("모임 구(ex. 강남구, 성동구)"),
                        fieldWithPath("[].meetingDatetime")
                                .type(JsonFieldType.STRING)
                                .description("모임 날짜")));
    }
}
