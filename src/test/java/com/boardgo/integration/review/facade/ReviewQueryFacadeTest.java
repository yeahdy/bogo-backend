package com.boardgo.integration.review.facade;

import static com.boardgo.common.constant.TimeConstant.REVIEWABLE_HOURS;
import static com.boardgo.domain.review.entity.enums.ReviewType.PRE_PROGRESS;
import static com.boardgo.integration.data.MeetingData.getMeetingEntityData;
import static com.boardgo.integration.data.UserInfoData.userInfoEntityData;
import static com.boardgo.integration.fixture.MeetingParticipantFixture.getLeaderMeetingParticipantEntity;
import static com.boardgo.integration.fixture.MeetingParticipantFixture.getOutMeetingParticipantEntity;
import static com.boardgo.integration.fixture.MeetingParticipantFixture.getParticipantMeetingParticipantEntity;
import static com.boardgo.integration.fixture.ReviewFixture.getReview;
import static com.boardgo.integration.fixture.UserInfoFixture.localUserInfoEntity;
import static com.boardgo.integration.fixture.UserInfoFixture.socialUserInfoEntity;
import static org.assertj.core.api.Assertions.assertThat;

import com.boardgo.domain.meeting.entity.MeetingEntity;
import com.boardgo.domain.meeting.entity.enums.MeetingState;
import com.boardgo.domain.meeting.repository.MeetingParticipantRepository;
import com.boardgo.domain.meeting.repository.MeetingRepository;
import com.boardgo.domain.review.repository.ReviewRepository;
import com.boardgo.domain.review.service.facade.ReviewQueryFacade;
import com.boardgo.domain.review.service.response.ReviewMeetingResponse;
import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.domain.user.entity.enums.ProviderType;
import com.boardgo.domain.user.repository.UserRepository;
import com.boardgo.integration.support.IntegrationTestSupport;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ReviewQueryFacadeTest extends IntegrationTestSupport {
    @Autowired private ReviewQueryFacade reviewQueryFacade;
    @Autowired private UserRepository userRepository;
    @Autowired private MeetingRepository meetingRepository;
    @Autowired private ReviewRepository reviewRepository;
    @Autowired private MeetingParticipantRepository meetingParticipantRepository;

    @Test
    @DisplayName("강퇴된 모임은 리뷰 모임 목록에 존재할 수 없다")
    void 강퇴된_모임은_리뷰_모임_목록에_존재할_수_없다() {
        // given
        UserInfoEntity participant = userRepository.save(localUserInfoEntity());
        Long meetingId = participateMeetingData();
        meetingParticipantRepository.save(
                getOutMeetingParticipantEntity(meetingId, participant.getId()));

        // when
        List<ReviewMeetingResponse> reviewMeetingList =
                reviewQueryFacade.getMeetingsToReview(PRE_PROGRESS, participant.getId());

        // then
        assertThat(reviewMeetingList).isEmpty();
    }

    @Test
    @DisplayName("모임이 종료된 지 3시간 이후된 모임만 리뷰 모임 목록에 존재한다")
    void 모임이_종료된_지_3시간_이후된_모임만_리뷰_모임_목록에_존재한다() {
        // given
        UserInfoEntity participant = userRepository.save(localUserInfoEntity());
        Long meetingId = participateMeetingData();
        meetingParticipantRepository.save(
                getParticipantMeetingParticipantEntity(meetingId, participant.getId()));
        // 모집중인 모임 추가
        UserInfoEntity leader =
                userRepository.save(userInfoEntityData("leader@email.com", "myname").build());
        meetingRepository.save(getMeetingEntityData(leader.getId()).build());

        // when
        List<ReviewMeetingResponse> reviewMeetingList =
                reviewQueryFacade.getMeetingsToReview(PRE_PROGRESS, participant.getId());

        // then
        assertThat(reviewMeetingList).isNotEmpty();
        for (ReviewMeetingResponse reviewMeeting : reviewMeetingList) {
            assertThat(reviewMeeting.meetingDatetime()).isBefore(LocalDateTime.now());

            LocalDateTime reviewableDatetime =
                    reviewMeeting.meetingDatetime().plusHours(REVIEWABLE_HOURS);
            assertThat(reviewableDatetime).isBeforeOrEqualTo(LocalDateTime.now());
        }
    }

    @Test
    @DisplayName("모임이 종료되었지만, 모임 날짜가 현재로 부터 3시간이 지나지 않았을 경우 리뷰 목록에 표출되지 않는다")
    void 모임이_종료되었지만_모임_날짜가_현재로_부터_3시간이_지나지_않았을_경우_리뷰_목록에_표출되지_않는다() {
        // given
        // 회원
        UserInfoEntity participant = userRepository.save(localUserInfoEntity());
        UserInfoEntity leader = userRepository.save(socialUserInfoEntity(ProviderType.KAKAO));
        // 모임
        MeetingEntity meeting =
                meetingRepository.save(
                        getMeetingEntityData(leader.getId())
                                .meetingDatetime(LocalDateTime.now())
                                .state(MeetingState.FINISH)
                                .build());
        // 모임참가
        meetingParticipantRepository.save(
                getLeaderMeetingParticipantEntity(meeting.getId(), leader.getId()));
        meetingParticipantRepository.save(
                getParticipantMeetingParticipantEntity(meeting.getId(), participant.getId()));

        // when
        List<ReviewMeetingResponse> reviewMeetingList =
                reviewQueryFacade.getMeetingsToReview(PRE_PROGRESS, participant.getId());

        // then
        assertThat(reviewMeetingList).isEmpty();

        LocalDateTime reviewableDatetime = meeting.getMeetingDatetime().plusHours(REVIEWABLE_HOURS);
        assertThat(reviewableDatetime).isAfterOrEqualTo(LocalDateTime.now());
    }

    @Test
    @DisplayName("모임에 참가한 참여자에게 모두 리뷰를 작성했을 경우 작성할 리뷰 모임 목록에 존재하지 않는다")
    void 모임에_참가한_참여자에게_모두_리뷰를_작성했을_경우_작성할_리뷰_모임_목록에_존재하지_않는다() {
        // given
        UserInfoEntity me = userRepository.save(localUserInfoEntity()); // 1L
        Long meetingId = participateMeetingData();

        // 참여자
        UserInfoEntity participant1 =
                userRepository.save(
                        userInfoEntityData("participant1@email.com", "participant1").build());
        UserInfoEntity participant2 =
                userRepository.save(
                        userInfoEntityData("participant2@email.com", "participant2").build());
        UserInfoEntity participant3 =
                userRepository.save(
                        userInfoEntityData("participant3@email.com", "participant3").build());
        // 모임참가(5명)
        meetingParticipantRepository.save(
                getParticipantMeetingParticipantEntity(meetingId, me.getId()));
        meetingParticipantRepository.save(
                getParticipantMeetingParticipantEntity(meetingId, participant1.getId()));
        meetingParticipantRepository.save(
                getParticipantMeetingParticipantEntity(meetingId, participant2.getId()));
        meetingParticipantRepository.save(
                getParticipantMeetingParticipantEntity(meetingId, participant3.getId()));
        // 리뷰(본인제외 모임참여저 5명 중 4명에게 리뷰 작성)
        Long leader = 2L;
        reviewRepository.save(getReview(me.getId(), leader, meetingId));
        reviewRepository.save(getReview(me.getId(), participant1.getId(), meetingId));
        reviewRepository.save(getReview(me.getId(), participant2.getId(), meetingId));
        reviewRepository.save(getReview(me.getId(), participant3.getId(), meetingId));

        // when
        List<ReviewMeetingResponse> reviewMeetingList =
                reviewQueryFacade.getMeetingsToReview(PRE_PROGRESS, me.getId());

        // then
        assertThat(reviewMeetingList).isEmpty();
    }

    @Test
    @DisplayName("모임에 참가한 참여자에게 모두 리뷰를 작성하지 않으면 작성할 리뷰 모임 목록에 존재한다")
    void 모임에_참가한_참여자에게_모두_리뷰를_작성하지_않으면_작성할_리뷰_모임_목록에_존재한다() {
        // given
        UserInfoEntity me = userRepository.save(localUserInfoEntity()); // 1L
        Long meetingId = participateMeetingData();

        // 참여자
        UserInfoEntity participant1 =
                userRepository.save(
                        userInfoEntityData("participant1@email.com", "participant1").build());
        UserInfoEntity participant2 =
                userRepository.save(
                        userInfoEntityData("participant2@email.com", "participant2").build());
        UserInfoEntity participant3 =
                userRepository.save(
                        userInfoEntityData("participant3@email.com", "participant3").build());
        // 모임참가(총 5명)
        meetingParticipantRepository.save(
                getParticipantMeetingParticipantEntity(meetingId, me.getId()));
        meetingParticipantRepository.save(
                getParticipantMeetingParticipantEntity(meetingId, participant1.getId()));
        meetingParticipantRepository.save(
                getParticipantMeetingParticipantEntity(meetingId, participant2.getId()));
        meetingParticipantRepository.save(
                getParticipantMeetingParticipantEntity(meetingId, participant3.getId()));
        // 리뷰(본인제외 모임참여저 5명 중 3명에게 리뷰 작성)
        reviewRepository.save(getReview(me.getId(), participant1.getId(), meetingId));
        reviewRepository.save(getReview(me.getId(), participant2.getId(), meetingId));
        reviewRepository.save(getReview(me.getId(), participant3.getId(), meetingId));

        // when
        List<ReviewMeetingResponse> reviewMeetingList =
                reviewQueryFacade.getMeetingsToReview(PRE_PROGRESS, me.getId());

        // then
        assertThat(reviewMeetingList).isNotEmpty();
        assertThat(reviewMeetingList.getFirst().id()).isEqualTo(meetingId);
    }

    private Long participateMeetingData() {
        // 회원
        UserInfoEntity leader = userRepository.save(socialUserInfoEntity(ProviderType.KAKAO));
        // 모임
        MeetingEntity meeting =
                meetingRepository.save(
                        getMeetingEntityData(leader.getId())
                                .meetingDatetime(LocalDateTime.now().minusDays(1))
                                .state(MeetingState.FINISH)
                                .build());
        // 모임참가
        meetingParticipantRepository.save(
                getLeaderMeetingParticipantEntity(meeting.getId(), leader.getId()));
        return meeting.getId();
    }
}
