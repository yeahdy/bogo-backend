package com.boardgo.integration.meeting.service;

import static com.boardgo.integration.data.MeetingData.getMeetingEntityData;
import static com.boardgo.integration.fixture.MeetingFixture.getProgressMeetingEntity;
import static com.boardgo.integration.fixture.MeetingParticipantFixture.getLeaderMeetingParticipantEntity;
import static com.boardgo.integration.fixture.MeetingParticipantFixture.getParticipantMeetingParticipantEntity;
import static com.boardgo.integration.fixture.UserInfoFixture.localUserInfoEntity;
import static com.boardgo.integration.fixture.UserInfoFixture.socialUserInfoEntity;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.boardgo.common.exception.CustomIllegalArgumentException;
import com.boardgo.domain.meeting.controller.request.MeetingParticipateRequest;
import com.boardgo.domain.meeting.entity.MeetingEntity;
import com.boardgo.domain.meeting.entity.enums.MeetingType;
import com.boardgo.domain.meeting.repository.MeetingParticipantRepository;
import com.boardgo.domain.meeting.repository.MeetingRepository;
import com.boardgo.domain.meeting.service.MeetingParticipantCommandUseCase;
import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.domain.user.entity.enums.ProviderType;
import com.boardgo.domain.user.repository.UserRepository;
import com.boardgo.integration.support.IntegrationTestSupport;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class MeetingParticipantCommandServiceV1Test extends IntegrationTestSupport {
    @Autowired private MeetingParticipantCommandUseCase participantCommandUseCase;
    @Autowired private UserRepository userRepository;
    @Autowired private MeetingRepository meetingRepository;
    @Autowired private MeetingParticipantRepository meetingParticipantRepository;

    @Test
    @DisplayName("이미 참여한 모임일 경우 예외가 발생한다")
    void 이미_참여한_모임일_경우_예외가_발생한다() {
        // given
        UserInfoEntity participants = getDuplicateMeetingParticipationData();
        Long meetingId = 1L;
        MeetingParticipateRequest participateRequest = new MeetingParticipateRequest(meetingId);

        // when
        // then
        assertThatThrownBy(
                        () ->
                                participantCommandUseCase.participateMeeting(
                                        participateRequest, participants.getId()))
                .isInstanceOf(CustomIllegalArgumentException.class)
                .hasMessageContaining("이미 참여된 모임");
        assertTrue(
                meetingParticipantRepository.existsByUserInfoIdAndMeetingId(
                        participants.getId(), meetingId));
    }

    private UserInfoEntity getDuplicateMeetingParticipationData() {
        UserInfoEntity participants = userRepository.save(socialUserInfoEntity(ProviderType.KAKAO));
        UserInfoEntity leader = userRepository.save(localUserInfoEntity());
        MeetingEntity meeting =
                meetingRepository.save(
                        getProgressMeetingEntity(leader.getId(), MeetingType.FREE, 3));
        meetingParticipantRepository.save(
                getLeaderMeetingParticipantEntity(
                        meeting.getId(), leader.getId())); // meeting.getId = 1L

        meetingParticipantRepository.save(
                getParticipantMeetingParticipantEntity(meeting.getId(), participants.getId()));
        return participants;
    }

    @Test
    @DisplayName("모임 날짜가 지났을 경우 예외가 발생한다")
    void 모임_날짜가_지났을_경우_예외가_발생한다() {
        // given
        UserInfoEntity participants = getAfterMeetingParticipationData();
        MeetingParticipateRequest participateRequest = new MeetingParticipateRequest(1L);

        // when
        // then
        assertThatThrownBy(
                        () ->
                                participantCommandUseCase.participateMeeting(
                                        participateRequest, participants.getId()))
                .isInstanceOf(CustomIllegalArgumentException.class)
                .hasMessageContaining("모임 날짜가 지난");
    }

    private UserInfoEntity getAfterMeetingParticipationData() {
        UserInfoEntity participants = userRepository.save(socialUserInfoEntity(ProviderType.KAKAO));
        UserInfoEntity leader = userRepository.save(localUserInfoEntity());
        MeetingEntity meeting =
                meetingRepository.save(
                        getMeetingEntityData(leader.getId())
                                .type(MeetingType.FREE)
                                .limitParticipant(3)
                                .meetingDatetime(LocalDateTime.now().minusDays(3))
                                .build());
        meetingParticipantRepository.save(
                getLeaderMeetingParticipantEntity(meeting.getId(), leader.getId()));

        return participants;
    }

    @Test
    @DisplayName("모임이 정원일 경우 예외가 발생한다")
    void 모임이_정원일_경우_예외가_발생한다() {
        // given
        UserInfoEntity participants = getLimitMeetingParticipationData();
        MeetingParticipateRequest participateRequest = new MeetingParticipateRequest(1L);

        // when
        // then
        assertThatThrownBy(
                        () ->
                                participantCommandUseCase.participateMeeting(
                                        participateRequest, participants.getId()))
                .isInstanceOf(CustomIllegalArgumentException.class)
                .hasMessageContaining("모임 정원");
    }

    private UserInfoEntity getLimitMeetingParticipationData() {
        UserInfoEntity participants = userRepository.save(socialUserInfoEntity(ProviderType.KAKAO));
        UserInfoEntity leader = userRepository.save(localUserInfoEntity());
        MeetingEntity meeting =
                meetingRepository.save(
                        getProgressMeetingEntity(leader.getId(), MeetingType.FREE, 1));
        meetingParticipantRepository.save(
                getLeaderMeetingParticipantEntity(meeting.getId(), leader.getId()));

        return participants;
    }

    @Test
    @DisplayName("수락 형식 모임일 경우 바로 모임에 참가할 수 없다")
    void 수락_형식_모임일_경우_바로_모임에_참가할_수_없다() {
        // given
        UserInfoEntity participants = getAcceptMeetingParticipationData();
        Long meetingId = 1L;
        MeetingParticipateRequest participateRequest = new MeetingParticipateRequest(meetingId);

        // when
        participantCommandUseCase.participateMeeting(participateRequest, participants.getId());

        // then
        assertFalse(
                meetingParticipantRepository.existsByUserInfoIdAndMeetingId(
                        participants.getId(), meetingId));
    }

    private UserInfoEntity getAcceptMeetingParticipationData() {
        UserInfoEntity participants = userRepository.save(socialUserInfoEntity(ProviderType.KAKAO));
        UserInfoEntity leader = userRepository.save(localUserInfoEntity());
        MeetingEntity meeting =
                meetingRepository.save(
                        getProgressMeetingEntity(leader.getId(), MeetingType.ACCEPT, 5));
        meetingParticipantRepository.save(
                getLeaderMeetingParticipantEntity(meeting.getId(), leader.getId()));

        return participants;
    }
}
