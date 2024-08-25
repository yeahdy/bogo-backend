package com.boardgo.integration.meeting.controller;

import static com.boardgo.common.constant.HeaderConstant.*;
import static com.boardgo.integration.fixture.MeetingFixture.*;
import static com.boardgo.integration.fixture.MeetingParticipantFixture.*;
import static com.boardgo.integration.fixture.UserInfoFixture.*;
import static io.restassured.RestAssured.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.*;

import com.boardgo.domain.meeting.controller.request.MeetingParticipateRequest;
import com.boardgo.domain.meeting.entity.MeetingEntity;
import com.boardgo.domain.meeting.entity.enums.MeetingType;
import com.boardgo.domain.meeting.repository.MeetingParticipantRepository;
import com.boardgo.domain.meeting.repository.MeetingRepository;
import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.domain.user.entity.enums.ProviderType;
import com.boardgo.domain.user.repository.UserRepository;
import com.boardgo.integration.support.RestDocsTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;

public class MeetingParticipantDocsTest extends RestDocsTestSupport {
    @Autowired private UserRepository userRepository;
    @Autowired private MeetingRepository meetingRepository;
    @Autowired private MeetingParticipantRepository meetingParticipantRepository;

    @Test
    @DisplayName("보드게임 모임 참가하기")
    void 보드게임_모임_참가하기() {
        userRepository.save(socialUserInfoEntity(ProviderType.KAKAO));
        UserInfoEntity user2 = userRepository.save(localUserInfoEntity());
        MeetingEntity meeting =
                meetingRepository.save(
                        getProgressMeetingEntity(user2.getId(), MeetingType.FREE, 3));
        meetingParticipantRepository.save(
                getLeaderMeetingParticipantEntity(meeting.getId(), user2.getId()));

        Long meetingId = 1L;
        MeetingParticipateRequest participateRequest = new MeetingParticipateRequest(meetingId);
        String requestJson = writeValueAsString(participateRequest);

        given(this.spec)
                .port(port)
                .log()
                .all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(API_VERSION_HEADER, "1")
                .header(AUTHORIZATION, testAccessToken)
                .body(requestJson)
                .filter(
                        document(
                                "meeting-participation",
                                preprocessResponse(prettyPrint()),
                                getParticipationRequestFieldsSnippet()))
                .when()
                .post("/meeting-participant/participation")
                .then()
                .log()
                .ifError()
                .statusCode(HttpStatus.CREATED.value());
    }

    RequestFieldsSnippet getParticipationRequestFieldsSnippet() {
        return requestFields(
                fieldWithPath("meetingId").type(JsonFieldType.NUMBER).description("모임 고유ID"));
    }
}
