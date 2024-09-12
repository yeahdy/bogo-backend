package com.boardgo.integration.meeting.controller;

import static com.boardgo.common.constant.HeaderConstant.*;
import static com.boardgo.integration.data.MeetingData.*;
import static io.restassured.RestAssured.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.*;

import com.boardgo.domain.meeting.entity.MeetingEntity;
import com.boardgo.domain.meeting.entity.MeetingLikeEntity;
import com.boardgo.domain.meeting.entity.MeetingParticipantEntity;
import com.boardgo.domain.meeting.entity.enums.MyPageMeetingFilter;
import com.boardgo.domain.meeting.repository.MeetingLikeRepository;
import com.boardgo.domain.meeting.repository.MeetingParticipantRepository;
import com.boardgo.domain.meeting.repository.MeetingRepository;
import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.domain.user.entity.enums.ProviderType;
import com.boardgo.domain.user.repository.UserRepository;
import com.boardgo.integration.fixture.MeetingLikeFixture;
import com.boardgo.integration.fixture.MeetingParticipantFixture;
import com.boardgo.integration.fixture.UserInfoFixture;
import com.boardgo.integration.support.RestDocsTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

public class MyPageMeetingDocsTest extends RestDocsTestSupport {
    @Autowired private UserRepository userRepository;
    @Autowired private MeetingRepository meetingRepository;
    @Autowired private MeetingLikeRepository meetingLikeRepository;
    @Autowired private MeetingParticipantRepository meetingParticipantRepository;

    @Test
    @DisplayName("마이페이지에 있는 모임들을 조회할 수 있다")
    void 마이페이지에_있는_모임들을_조회할_수_있다() {
        // given
        UserInfoEntity userInfoEntity = UserInfoFixture.localUserInfoEntity();
        UserInfoEntity savedUser = userRepository.save(userInfoEntity);
        UserInfoEntity userInfoEntity2 = UserInfoFixture.socialUserInfoEntity(ProviderType.KAKAO);
        UserInfoEntity savedUser2 = userRepository.save(userInfoEntity2);
        MeetingEntity meetingEntity1 = getMeetingEntityData(savedUser.getId()).build();
        MeetingEntity meetingEntity2 =
                getMeetingEntityData(savedUser.getId()).limitParticipant(10).build();
        MeetingEntity savedMeeting1 = meetingRepository.save(meetingEntity1);
        MeetingEntity savedMeeting2 = meetingRepository.save(meetingEntity2);
        MeetingParticipantEntity leaderMeetingParticipantEntity1 =
                MeetingParticipantFixture.getLeaderMeetingParticipantEntity(
                        savedMeeting1.getId(), savedUser.getId());
        MeetingParticipantEntity leaderMeetingParticipantEntity2 =
                MeetingParticipantFixture.getLeaderMeetingParticipantEntity(
                        savedMeeting2.getId(), savedUser.getId());
        MeetingParticipantEntity participantMeetingParticipantEntity =
                MeetingParticipantFixture.getParticipantMeetingParticipantEntity(
                        savedMeeting1.getId(), savedUser2.getId());
        meetingParticipantRepository.save(leaderMeetingParticipantEntity1);
        meetingParticipantRepository.save(leaderMeetingParticipantEntity2);
        meetingParticipantRepository.save(participantMeetingParticipantEntity);
        // when
        // then
        given(this.spec)
                .log()
                .all()
                .port(port)
                .header(API_VERSION_HEADER, "1")
                .header(AUTHORIZATION, testAccessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .queryParam("filter", MyPageMeetingFilter.PARTICIPANT)
                .filter(
                        document(
                                "my-meeting",
                                queryParameters(
                                        parameterWithName("filter")
                                                .description(
                                                        "모임 필터 > 참여 중 모임 : PARTICIPANT / 종료된 모임 : FINISH / 내가 만든 모임: CREATE")),
                                responseFields(
                                        fieldWithPath("[].meetingId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("모임 id"),
                                        fieldWithPath("[].writerId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("작성자 id"),
                                        fieldWithPath("[].title")
                                                .type(JsonFieldType.STRING)
                                                .description("모임 제목"),
                                        fieldWithPath("[].thumbnail")
                                                .type(JsonFieldType.STRING)
                                                .description("모임 썸네일"),
                                        fieldWithPath("[].detailAddress")
                                                .type(JsonFieldType.STRING)
                                                .description("모임의 상세 주소"),
                                        fieldWithPath("[].meetingDatetime")
                                                .type(JsonFieldType.STRING)
                                                .description("모임 시간"),
                                        fieldWithPath("[].limitParticipant")
                                                .type(JsonFieldType.NUMBER)
                                                .description("최대 참가자 수"),
                                        fieldWithPath("[].currentParticipant")
                                                .type(JsonFieldType.NUMBER)
                                                .description("현재 참가자 수"))))
                .when()
                .get("/my/meeting")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("마이페이지에서 찜한 목록을 가져올 수 있다")
    void 마이페이지에서_찜한_목록을_가져올_수_있다() {
        // given
        UserInfoEntity userInfoEntity = UserInfoFixture.localUserInfoEntity();
        UserInfoEntity savedUser = userRepository.save(userInfoEntity);

        MeetingEntity meetingEntity1 = getMeetingEntityData(2L).limitParticipant(10).build();
        MeetingEntity savedMeeting1 = meetingRepository.save(meetingEntity1);
        MeetingEntity meetingEntity2 = getMeetingEntityData(3L).limitParticipant(20).build();
        MeetingEntity savedMeeting2 = meetingRepository.save(meetingEntity2);

        MeetingLikeEntity meetingLike1 =
                MeetingLikeFixture.getMeetingLike(savedUser.getId(), savedMeeting1.getId());
        MeetingLikeEntity meetingLike2 =
                MeetingLikeFixture.getMeetingLike(savedUser.getId(), savedMeeting2.getId());
        MeetingLikeEntity savedMeetingLike1 = meetingLikeRepository.save(meetingLike1);
        MeetingLikeEntity savedMeetingLike2 = meetingLikeRepository.save(meetingLike2);

        MeetingParticipantEntity participantMeetingParticipantEntity1 =
                MeetingParticipantFixture.getParticipantMeetingParticipantEntity(
                        meetingLike1.getMeetingId(), 2L);
        MeetingParticipantEntity participantMeetingParticipantEntity2 =
                MeetingParticipantFixture.getParticipantMeetingParticipantEntity(
                        meetingLike2.getMeetingId(), 2L);
        MeetingParticipantEntity participantMeetingParticipantEntity3 =
                MeetingParticipantFixture.getParticipantMeetingParticipantEntity(
                        meetingLike2.getMeetingId(), 3L);
        meetingParticipantRepository.save(participantMeetingParticipantEntity1);
        meetingParticipantRepository.save(participantMeetingParticipantEntity2);
        meetingParticipantRepository.save(participantMeetingParticipantEntity3);

        // when
        // then
        given(this.spec)
                .log()
                .all()
                .port(port)
                .header(API_VERSION_HEADER, "1")
                .header(AUTHORIZATION, testAccessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .filter(
                        document(
                                "my-liked-meeting",
                                responseFields(
                                        fieldWithPath("[].meetingId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("모임 id")
                                                .optional(),
                                        fieldWithPath("[].title")
                                                .type(JsonFieldType.STRING)
                                                .description("모임 제목"),
                                        fieldWithPath("[].thumbnail")
                                                .type(JsonFieldType.STRING)
                                                .description("모임 썸네일"),
                                        fieldWithPath("[].locationName")
                                                .type(JsonFieldType.STRING)
                                                .description("모임의 장소 이름"),
                                        fieldWithPath("[].meetingDatetime")
                                                .type(JsonFieldType.STRING)
                                                .description("모임 시간"),
                                        fieldWithPath("[].limitParticipant")
                                                .type(JsonFieldType.NUMBER)
                                                .description("최대 참가자 수"),
                                        fieldWithPath("[].currentParticipant")
                                                .type(JsonFieldType.NUMBER)
                                                .description("현재 참가자 수"))))
                .when()
                .get("/my/meeting/like")
                .then()
                .statusCode(HttpStatus.OK.value());
    }
}
