package com.boardgo.integration.meeting.service;

import static org.assertj.core.api.Assertions.*;

import com.boardgo.domain.meeting.entity.MeetingEntity;
import com.boardgo.domain.meeting.entity.MeetingParticipantEntity;
import com.boardgo.domain.meeting.entity.enums.MeetingType;
import com.boardgo.domain.meeting.entity.enums.MyPageMeetingFilter;
import com.boardgo.domain.meeting.repository.MeetingParticipantRepository;
import com.boardgo.domain.meeting.repository.MeetingRepository;
import com.boardgo.domain.meeting.service.MyPageMeetingQueryUseCase;
import com.boardgo.domain.meeting.service.response.MeetingMyPageResponse;
import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.domain.user.entity.enums.ProviderType;
import com.boardgo.domain.user.repository.UserRepository;
import com.boardgo.domain.user.service.dto.CustomUserDetails;
import com.boardgo.integration.fixture.MeetingFixture;
import com.boardgo.integration.fixture.MeetingParticipantFixture;
import com.boardgo.integration.fixture.UserInfoFixture;
import com.boardgo.integration.support.IntegrationTestSupport;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class MyPageMeetingQueryServiceV1Test extends IntegrationTestSupport {
    @Autowired private UserRepository userRepository;
    @Autowired private MeetingRepository meetingRepository;
    @Autowired private MeetingParticipantRepository meetingParticipantRepository;
    @Autowired private MyPageMeetingQueryUseCase myPageMeetingQueryUseCase;

    @Test
    @DisplayName("내가 만든 모임을 가져올 수 있다")
    void 내가_만든_모임을_가져올_수_있다() {
        // given
        UserInfoEntity userInfoEntity = UserInfoFixture.localUserInfoEntity();
        UserInfoEntity savedUser = userRepository.save(userInfoEntity);
        UserInfoEntity userInfoEntity2 = UserInfoFixture.socialUserInfoEntity(ProviderType.KAKAO);
        UserInfoEntity savedUser2 = userRepository.save(userInfoEntity2);
        setSecurityContext(savedUser.getId(), savedUser.getPassword());
        MeetingEntity meetingEntity1 =
                MeetingFixture.getProgressMeetingEntity(savedUser.getId(), MeetingType.FREE, 5);
        MeetingEntity meetingEntity2 =
                MeetingFixture.getProgressMeetingEntity(savedUser.getId(), MeetingType.FREE, 10);
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
        List<MeetingMyPageResponse> result =
                myPageMeetingQueryUseCase.findByFilter(MyPageMeetingFilter.CREATE);
        // then
        assertThat(result)
                .extracting(MeetingMyPageResponse::meetingId)
                .containsExactlyInAnyOrder(savedMeeting1.getId(), savedMeeting2.getId());
        assertThat(result)
                .extracting(MeetingMyPageResponse::currentParticipant)
                .containsExactlyInAnyOrder(1, 2);
    }

    @Test
    @DisplayName("내가 참여한 모임을 가져올 수 있다")
    void 내가_참여한_모임을_가져올_수_있다() {
        // given
        UserInfoEntity userInfoEntity = UserInfoFixture.localUserInfoEntity();
        UserInfoEntity savedUser = userRepository.save(userInfoEntity);
        UserInfoEntity userInfoEntity2 = UserInfoFixture.socialUserInfoEntity(ProviderType.KAKAO);
        UserInfoEntity savedUser2 = userRepository.save(userInfoEntity2);
        setSecurityContext(savedUser2.getId(), savedUser2.getPassword());
        MeetingEntity meetingEntity1 =
                MeetingFixture.getProgressMeetingEntity(savedUser.getId(), MeetingType.FREE, 5);
        MeetingEntity meetingEntity2 =
                MeetingFixture.getProgressMeetingEntity(savedUser.getId(), MeetingType.FREE, 10);
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
        List<MeetingMyPageResponse> result =
                myPageMeetingQueryUseCase.findByFilter(MyPageMeetingFilter.PARTICIPANT);
        // then
        assertThat(result)
                .extracting(MeetingMyPageResponse::meetingId)
                .containsExactlyInAnyOrder(savedMeeting1.getId());
        assertThat(result)
                .extracting(MeetingMyPageResponse::currentParticipant)
                .containsExactlyInAnyOrder(2);
    }

    @Test
    @DisplayName("종료된 모임을 가져올 수 있다")
    void 종료된_모임을_가져올_수_있다() {
        UserInfoEntity userInfoEntity = UserInfoFixture.localUserInfoEntity();
        UserInfoEntity savedUser = userRepository.save(userInfoEntity);
        UserInfoEntity userInfoEntity2 = UserInfoFixture.socialUserInfoEntity(ProviderType.KAKAO);
        UserInfoEntity savedUser2 = userRepository.save(userInfoEntity2);
        setSecurityContext(savedUser.getId(), savedUser.getPassword());
        MeetingEntity meetingEntity1 =
                MeetingFixture.getFinishMeetingEntity(savedUser.getId(), MeetingType.FREE, 5);
        MeetingEntity meetingEntity2 =
                MeetingFixture.getProgressMeetingEntity(savedUser.getId(), MeetingType.FREE, 10);
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
        List<MeetingMyPageResponse> result =
                myPageMeetingQueryUseCase.findByFilter(MyPageMeetingFilter.FINISH);
        // then
        assertThat(result)
                .extracting(MeetingMyPageResponse::meetingId)
                .containsExactlyInAnyOrder(savedMeeting1.getId());
        assertThat(result)
                .extracting(MeetingMyPageResponse::currentParticipant)
                .containsExactlyInAnyOrder(2);
    }

    @Test
    @DisplayName("마이페이지에 있는 모임 조회 중 없으면 표시할 데이터가 없으면 빈 리스트를 반환한다")
    void 마이페이지에_있는_모임_조회_중_없으면_표시할_데이터가_없으면_빈_리스트를_반환한다() {
        // given
        UserInfoEntity userInfoEntity = UserInfoFixture.localUserInfoEntity();
        UserInfoEntity savedUser = userRepository.save(userInfoEntity);
        setSecurityContext(savedUser.getId(), savedUser.getPassword());
        // when
        List<MeetingMyPageResponse> result =
                myPageMeetingQueryUseCase.findByFilter(MyPageMeetingFilter.FINISH);
        // then
        assertThat(result).isEmpty();
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
}
