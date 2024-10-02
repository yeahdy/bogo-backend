package com.boardgo.integration.meeting.controller;

import static com.boardgo.common.constant.HeaderConstant.*;
import static com.boardgo.integration.data.MeetingData.*;
import static com.boardgo.integration.data.UserInfoData.*;
import static com.boardgo.integration.fixture.MeetingParticipantFixture.*;
import static com.boardgo.integration.fixture.UserInfoFixture.*;
import static io.restassured.RestAssured.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;

import com.boardgo.domain.meeting.controller.request.MeetingOutRequest;
import com.boardgo.domain.meeting.controller.request.MeetingParticipateRequest;
import com.boardgo.domain.meeting.entity.MeetingEntity;
import com.boardgo.domain.meeting.entity.MeetingParticipantEntity;
import com.boardgo.domain.meeting.repository.MeetingParticipantRepository;
import com.boardgo.domain.meeting.repository.MeetingRepository;
import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.domain.user.entity.enums.ProviderType;
import com.boardgo.domain.user.repository.UserRepository;
import com.boardgo.integration.support.RestDocsTestSupport;

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
                        getMeetingEntityData(user2.getId()).limitParticipant(3).build());
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

    private RequestFieldsSnippet getParticipationRequestFieldsSnippet() {
        return requestFields(
                fieldWithPath("meetingId").type(JsonFieldType.NUMBER).description("모임 고유ID"));
    }

    @Test
    @DisplayName("사용자가 방장에 의해 강퇴된 사람인지 판별할 수 있다")
    void 사용자가_방장에_의해_강퇴된_사람인지_판별할_수_있다() {
        UserInfoEntity userInfoEntity = localUserInfoEntity();
        UserInfoEntity savedUser = userRepository.save(userInfoEntity);
        MeetingEntity meetingEntity =
                getMeetingEntityData(savedUser.getId()).limitParticipant(10).build();
        MeetingEntity savedMeeting = meetingRepository.save(meetingEntity);
        MeetingParticipantEntity participantMeetingParticipantEntity =
                getOutMeetingParticipantEntity(savedMeeting.getId(), savedUser.getId());
        meetingParticipantRepository.save(participantMeetingParticipantEntity);

        given(this.spec)
                .log()
                .all()
                .port(port)
                .header(API_VERSION_HEADER, "1")
                .header(AUTHORIZATION, testAccessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParams("meetingId", 1)
                .filter(
                        document(
                                "participant-out-check",
                                pathParameters(parameterWithName("meetingId").description("모임 id")),
                                responseFields(
                                        fieldWithPath("outState")
                                                .description("방장이 내보낸 사람이면 'OUT' / 아니면 null"))))
                .when()
                .get("/meeting-participant/out/{meetingId}")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("사용자는 모임에 참석한 유저 리스트를 볼 수 있다")
    void 사용자는_모임에_참석한_유저_리스트를_볼_수_있다() {
        //given
        UserInfoEntity leader =
            userRepository.save(userInfoEntityData("leader@test.com", "Leader").build());
        UserInfoEntity participant =
            userRepository.save(userInfoEntityData("bear@test.com", "bear").build());
        MeetingEntity meetingEntity =
            getMeetingEntityData(leader.getId()).limitParticipant(10).build();
        MeetingEntity savedMeeting = meetingRepository.save(meetingEntity);
        meetingParticipantRepository.save(
            getLeaderMeetingParticipantEntity(savedMeeting.getId(), leader.getId()));
        meetingParticipantRepository.save(
            getParticipantMeetingParticipantEntity(savedMeeting.getId(), participant.getId()));
        //when
        //then
        given(this.spec)
            .log()
            .all()
            .port(port)
            .header(API_VERSION_HEADER, "1")
            .header(AUTHORIZATION, testAccessToken)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .pathParams("meetingId", meetingEntity.getId())
            .filter(
                document(
                    "participant-list",
                    pathParameters(parameterWithName("meetingId").description("모임 id")),
                    responseFields(
                        fieldWithPath("[].userId")
                            .type(JsonFieldType.NUMBER)
                            .description("유저 id"),
                        fieldWithPath("[].profileImage")
                            .type(JsonFieldType.STRING)
                            .description("유저 프로필 이미지"),
                        fieldWithPath("[].nickname")
                            .type(JsonFieldType.STRING)
                            .description("유저 닉네임"),
                        fieldWithPath("[].type")
                            .type(JsonFieldType.STRING)
                            .description("유저 타입 (LEADER / PARTICIPANT)")
                    )))
            .when()
            .get("/meeting-participant/{meetingId}")
            .then()
            .statusCode(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("모임 내보내기")
    void 모임을_내보내기() {
        UserInfoEntity leader =
                userRepository.save(userInfoEntityData("leader@test.com", "Leader").build());
        UserInfoEntity participant =
                userRepository.save(userInfoEntityData("bear@test.com", "bear").build());
        MeetingEntity meetingEntity = getMeetingEntityData(participant.getId()).build();
        MeetingEntity meeting = meetingRepository.save(meetingEntity);
        meetingParticipantRepository.save(
                getLeaderMeetingParticipantEntity(meeting.getId(), leader.getId()));
        meetingParticipantRepository.save(
                getParticipantMeetingParticipantEntity(meeting.getId(), participant.getId()));

        MeetingOutRequest participateRequest =
                new MeetingOutRequest(meetingEntity.getId(), "PROGRESS");
        String requestJson = writeValueAsString(participateRequest);

        given(this.spec)
                .log()
                .all()
                .port(port)
                .header(API_VERSION_HEADER, "1")
                .header(AUTHORIZATION, testAccessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParams("userId", participant.getId())
                .body(requestJson)
                .filter(
                        document(
                                "patch-out-meeting",
                                pathParameters(parameterWithName("userId").description("회원 id")),
                                getOutMeetingRequestFieldsSnippet()))
                .when()
                .patch("/meeting-participant/out/{userId}")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("모임 나가기")
    void 모임_나가기() {
        UserInfoEntity leader =
                userRepository.save(userInfoEntityData("leader@test.com", "Leader").build());
        UserInfoEntity participant =
                userRepository.save(userInfoEntityData("bear@test.com", "bear").build());
        MeetingEntity meetingEntity = getMeetingEntityData(participant.getId()).build();
        MeetingEntity meeting = meetingRepository.save(meetingEntity);
        meetingParticipantRepository.save(
                getLeaderMeetingParticipantEntity(meeting.getId(), leader.getId()));
        meetingParticipantRepository.save(
                getParticipantMeetingParticipantEntity(meeting.getId(), participant.getId()));

        MeetingOutRequest participateRequest =
                new MeetingOutRequest(meetingEntity.getId(), "PROGRESS");
        String requestJson = writeValueAsString(participateRequest);

        given(this.spec)
                .log()
                .all()
                .port(port)
                .header(API_VERSION_HEADER, "1")
                .header(AUTHORIZATION, testAccessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(requestJson)
                .filter(document("patch-out-my-meeting", getOutMeetingRequestFieldsSnippet()))
                .when()
                .patch("/meeting-participant/out")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    private RequestFieldsSnippet getOutMeetingRequestFieldsSnippet() {
        return requestFields(
                fieldWithPath("meetingId").type(JsonFieldType.NUMBER).description("모임 고유ID"),
                fieldWithPath("meetingState").type(JsonFieldType.STRING).description("모임 진행 상태"));
    }
}
