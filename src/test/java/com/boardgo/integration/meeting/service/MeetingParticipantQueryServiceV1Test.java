package com.boardgo.integration.meeting.service;

import static com.boardgo.integration.data.MeetingData.getMeetingEntityData;
import static com.boardgo.integration.data.UserInfoData.userInfoEntityData;
import static com.boardgo.integration.fixture.MeetingParticipantFixture.getLeaderMeetingParticipantEntity;
import static com.boardgo.integration.fixture.MeetingParticipantFixture.getOutMeetingParticipantEntity;
import static com.boardgo.integration.fixture.MeetingParticipantFixture.getParticipantMeetingParticipantEntity;
import static com.boardgo.integration.fixture.UserInfoFixture.localUserInfoEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import com.boardgo.common.exception.CustomIllegalArgumentException;
import com.boardgo.domain.meeting.entity.MeetingEntity;
import com.boardgo.domain.meeting.entity.MeetingParticipantEntity;
import com.boardgo.domain.meeting.entity.enums.ParticipantType;
import com.boardgo.domain.meeting.repository.MeetingParticipantRepository;
import com.boardgo.domain.meeting.repository.MeetingRepository;
import com.boardgo.domain.meeting.service.MeetingParticipantQueryUseCase;
import com.boardgo.domain.meeting.service.response.ParticipantOutResponse;
import com.boardgo.domain.meeting.service.response.UserParticipantResponse;
import com.boardgo.domain.review.service.response.ReviewMeetingParticipantsResponse;
import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.domain.user.repository.UserRepository;
import com.boardgo.domain.user.service.response.CustomUserDetails;
import com.boardgo.integration.support.IntegrationTestSupport;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class MeetingParticipantQueryServiceV1Test extends IntegrationTestSupport {
    @Autowired private UserRepository userRepository;
    @Autowired private MeetingRepository meetingRepository;
    @Autowired private MeetingParticipantRepository meetingParticipantRepository;

    @Autowired private MeetingParticipantQueryUseCase meetingParticipantQueryUseCase;

    @Test
    @DisplayName("모임에 참여한 회원 목록을 조회한다")
    void 모임에_참여한_회원_목록을_조회한다() {
        // given
        List<UserInfoEntity> userInfoEntities = new ArrayList<>();
        int userNumber = 7;
        for (int i = 0; i < userNumber; i++) {
            UserInfoEntity userInfo =
                    userInfoEntityData("participate" + i + "@naver.com", "participate" + i).build();
            userInfoEntities.add(userInfo);
        }
        userRepository.saveAll(userInfoEntities);

        Long meetingId = 1L;
        for (int i = 0; i < userNumber / 2; i++) {
            meetingParticipantRepository.save(
                    getLeaderMeetingParticipantEntity(meetingId, userInfoEntities.get(i).getId()));
        }
        for (int i = 3; i < userNumber - 1; i++) {
            meetingParticipantRepository.save(
                    getParticipantMeetingParticipantEntity(
                            meetingId, userInfoEntities.get(i).getId()));
        }
        meetingParticipantRepository.save(
                getOutMeetingParticipantEntity(meetingId, (long) (userNumber)));

        // when
        List<UserParticipantResponse> userParticipantResponse =
                meetingParticipantQueryUseCase.findByMeetingId(meetingId);

        // then
        userParticipantResponse.forEach(
                userParticipant -> {
                    assertThat(userParticipant.type()).isNotEqualTo(ParticipantType.OUT);
                });
    }

    @Test
    @DisplayName("방장에 의해 나가진 사용자를 구별할 수 있다")
    void 방장에_의해_나가진_사용자를_구별할_수_있다() {
        // given
        UserInfoEntity userInfoEntity = localUserInfoEntity();
        UserInfoEntity savedUser = userRepository.save(userInfoEntity);
        setSecurityContext(savedUser.getId(), savedUser.getPassword());
        MeetingEntity meetingEntity =
                getMeetingEntityData(savedUser.getId()).limitParticipant(10).build();
        MeetingEntity savedMeeting = meetingRepository.save(meetingEntity);
        MeetingParticipantEntity participantMeetingParticipantEntity =
                getOutMeetingParticipantEntity(savedMeeting.getId(), savedUser.getId());
        meetingParticipantRepository.save(participantMeetingParticipantEntity);
        // when
        ParticipantOutResponse outState =
                meetingParticipantQueryUseCase.getOutState(savedMeeting.getId());
        // then
        Assertions.assertThat(outState.outState()).isEqualTo("OUT");
    }

    private void setSecurityContext(Long userId, String password) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        UserInfoEntity userInfoEntity =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found"));
        CustomUserDetails customUserDetails = new CustomUserDetails(userInfoEntity);

        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        customUserDetails, password, customUserDetails.getAuthorities());
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
    }

    @Test
    @DisplayName("한명이라도 모임에 함께 참여하지 않은 사람이 있을 경우 예외를 반환한다")
    void 한명이라도_모임에_함께_참여하지_않은_사람이_있을_경우_예외를_반환한다() {
        // given
        Long leader = 1L;
        MeetingEntity meeting = meetingRepository.save(getMeetingEntityData(leader).build());
        MeetingParticipantEntity participant1 =
                getParticipantMeetingParticipantEntity(meeting.getId(), 2L);
        meetingParticipantRepository.save(participant1);
        MeetingParticipantEntity participant2 =
                getParticipantMeetingParticipantEntity(meeting.getId(), 3L);
        meetingParticipantRepository.save(participant2);
        MeetingParticipantEntity outParticipant =
                getOutMeetingParticipantEntity(meeting.getId(), 4L);
        meetingParticipantRepository.save(outParticipant);

        // when
        // then
        assertThatThrownBy(
                        () ->
                                meetingParticipantQueryUseCase.checkMeetingTogether(
                                        meeting.getId(),
                                        List.of(
                                                participant1.getUserInfoId(),
                                                participant2.getUserInfoId(),
                                                outParticipant.getUserInfoId())))
                .isInstanceOf(CustomIllegalArgumentException.class)
                .hasMessageContaining("모임을 함께 참여하지 않았습니다");
    }

    @Test
    @DisplayName("모임에 함께 참여한 참여자들 중 리뷰를 작성하지 않는 참여자 조회하기")
    void 모임에_함께_참여한_참여자들_중_리뷰를_작성하지_않는_참여자_조회하기() {
        // given
        // 회원
        List<UserInfoEntity> userInfoEntities = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            UserInfoEntity userInfo =
                    userInfoEntityData("participate" + i + "@naver.com", "participate" + i).build();
            userInfoEntities.add(userInfo);
        }
        userRepository.saveAll(userInfoEntities);
        // 이미 작성된 리뷰의 회원 id
        List<Long> createdReviewIds = new ArrayList<>();
        createdReviewIds.add(userInfoEntities.get(0).getId());
        createdReviewIds.add(userInfoEntities.get(1).getId());
        createdReviewIds.add(userInfoEntities.get(2).getId());
        // 모임 참여
        Long meetingId = 1L;
        userInfoEntities.forEach(
                userInfoEntity ->
                        meetingParticipantRepository.save(
                                getParticipantMeetingParticipantEntity(
                                        meetingId, userInfoEntity.getId())));

        // when
        List<ReviewMeetingParticipantsResponse> participantsToReview =
                meetingParticipantQueryUseCase.findMeetingParticipantsToReview(
                        createdReviewIds, meetingId);

        // then
        assertThat(participantsToReview).isNotEmpty();
        assertThat(participantsToReview)
                .hasSize(2)
                .extracting("revieweeId", "revieweeName")
                .containsExactly(
                        tuple(
                                userInfoEntities.get(3).getId(),
                                userInfoEntities.get(3).getNickName()),
                        tuple(
                                userInfoEntities.get(4).getId(),
                                userInfoEntities.get(4).getNickName()));
        // 이미 작성된 리뷰의 회원
        assertThat(participantsToReview)
                .extracting("revieweeId", "revieweeName")
                .doesNotContain(
                        tuple(
                                userInfoEntities.get(0).getId(),
                                userInfoEntities.get(0).getNickName()),
                        tuple(
                                userInfoEntities.get(1).getId(),
                                userInfoEntities.get(1).getNickName()),
                        tuple(
                                userInfoEntities.get(2).getId(),
                                userInfoEntities.get(2).getNickName()));
    }
}
