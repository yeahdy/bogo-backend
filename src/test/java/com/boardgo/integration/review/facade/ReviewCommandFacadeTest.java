package com.boardgo.integration.review.facade;

import static com.boardgo.domain.notification.entity.MessageType.REVIEW_RECEIVED;
import static com.boardgo.integration.data.MeetingData.getMeetingEntityData;
import static com.boardgo.integration.data.UserInfoData.userInfoEntityData;
import static com.boardgo.integration.fixture.MeetingParticipantFixture.getLeaderMeetingParticipantEntity;
import static com.boardgo.integration.fixture.MeetingParticipantFixture.getParticipantMeetingParticipantEntity;
import static com.boardgo.integration.fixture.ReviewFixture.getReview;
import static com.boardgo.integration.fixture.UserInfoFixture.localUserInfoEntity;
import static com.boardgo.integration.fixture.UserInfoFixture.socialUserInfoEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.boardgo.common.exception.CustomIllegalArgumentException;
import com.boardgo.common.exception.DuplicateException;
import com.boardgo.domain.meeting.entity.MeetingEntity;
import com.boardgo.domain.meeting.entity.enums.MeetingState;
import com.boardgo.domain.meeting.repository.MeetingParticipantRepository;
import com.boardgo.domain.meeting.repository.MeetingRepository;
import com.boardgo.domain.notification.entity.NotificationEntity;
import com.boardgo.domain.notification.entity.NotificationMessage;
import com.boardgo.domain.notification.repository.NotificationRepository;
import com.boardgo.domain.review.controller.request.ReviewCreateRequest;
import com.boardgo.domain.review.repository.ReviewRepository;
import com.boardgo.domain.review.service.facade.ReviewCommandFacade;
import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.domain.user.entity.enums.ProviderType;
import com.boardgo.domain.user.repository.UserRepository;
import com.boardgo.integration.support.IntegrationTestSupport;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ReviewCommandFacadeTest extends IntegrationTestSupport {

    @Autowired private ReviewCommandFacade reviewCommandFacade;
    @Autowired private UserRepository userRepository;
    @Autowired private MeetingRepository meetingRepository;
    @Autowired private ReviewRepository reviewRepository;
    @Autowired private MeetingParticipantRepository meetingParticipantRepository;
    @Autowired private NotificationRepository notificationRepository;

    @Test
    @DisplayName("리뷰 작성 시 종료된 모임이 아니면 예외가 발생한다")
    void 리뷰_작성_시_종료된_모임이_아니면_예외가_발생한다() {
        // given
        Long reviewerId = 1L;
        Long revieweeId = 2L;
        // 진행 중인 모임
        MeetingEntity meeting = meetingRepository.save(getMeetingEntityData(reviewerId).build());
        Long meetingId = meeting.getId();
        meetingParticipantRepository.save(getLeaderMeetingParticipantEntity(meetingId, reviewerId));
        int rating = 5;
        ReviewCreateRequest request =
                new ReviewCreateRequest(revieweeId, meetingId, rating, List.of(3L, 4L, 5L, 6L));

        // when
        // then
        assertThatThrownBy(() -> reviewCommandFacade.create(request, reviewerId))
                .isInstanceOf(CustomIllegalArgumentException.class)
                .hasMessageContaining("종료된 모임이 아닙니다");
    }

    @Test
    @DisplayName("리뷰 작성 시 이미 작성된 리뷰일 경우 예외갸 발생한다")
    void 리뷰_작성_시_이미_작성된_리뷰일_경우_예외갸_발생한다() {
        UserInfoEntity me = userRepository.save(localUserInfoEntity()); // 1L
        Long meetingId = participateMeetingData();

        // 모임 참여자(총 3명)
        UserInfoEntity participant1 =
                userRepository.save(
                        userInfoEntityData("participant1@email.com", "participant1").build());
        meetingParticipantRepository.save(
                getParticipantMeetingParticipantEntity(meetingId, me.getId()));
        meetingParticipantRepository.save(
                getParticipantMeetingParticipantEntity(meetingId, participant1.getId()));
        // 리뷰
        reviewRepository.save(getReview(me.getId(), participant1.getId(), meetingId));

        ReviewCreateRequest request =
                new ReviewCreateRequest(
                        participant1.getId(), meetingId, 5, List.of(3L, 4L, 5L, 6L));
        // when
        // then
        assertThatThrownBy(() -> reviewCommandFacade.create(request, me.getId()))
                .isInstanceOf(DuplicateException.class)
                .hasMessageContaining("이미 작성된 리뷰 입니다");
    }

    @Test
    @DisplayName("리뷰 작성 시 모임에 함께 참여한 참여자가 아닌 경우 예외가 발생한다.")
    void 리뷰_작성_시_모임에_함께_참여한_참여자가_아닌_경우_예외가_발생한다() {
        // given
        UserInfoEntity me = userRepository.save(localUserInfoEntity()); // 1L
        Long meetingId = participateMeetingData();

        // 모임 참여자(총 3명)
        UserInfoEntity participant1 =
                userRepository.save(
                        userInfoEntityData("participant1@email.com", "participant1").build());
        meetingParticipantRepository.save(
                getParticipantMeetingParticipantEntity(meetingId, me.getId()));
        meetingParticipantRepository.save(
                getParticipantMeetingParticipantEntity(meetingId, participant1.getId()));
        // 모임 미참여자
        UserInfoEntity participant2 =
                userRepository.save(
                        userInfoEntityData("participant2@email.com", "participant2").build());
        ReviewCreateRequest request =
                new ReviewCreateRequest(
                        participant2.getId(), meetingId, 5, List.of(3L, 4L, 5L, 6L));

        // when
        // then
        assertThatThrownBy(() -> reviewCommandFacade.create(request, me.getId()))
                .isInstanceOf(CustomIllegalArgumentException.class)
                .hasMessageContaining("모임을 함께 참여하지 않았습니다");
    }

    @Test
    @DisplayName("리뷰를_작성하면_리뷰를_받는_사람에게_알림메세지가_발송된다")
    void 리뷰를_작성하면_리뷰를_받는_사람에게_알림메세지가_발송된다() {
        // given
        Long meetingId = participateMeetingData();
        UserInfoEntity reviewee =
                userRepository.save(userInfoEntityData("reviewee@naver.com", "리뷰받아용").build());
        meetingParticipantRepository.save(
                getParticipantMeetingParticipantEntity(meetingId, reviewee.getId()));
        ReviewCreateRequest request =
                new ReviewCreateRequest(reviewee.getId(), meetingId, 5, List.of(1L, 3L, 4L, 5L));

        // when
        Long user1 = 1L;
        reviewCommandFacade.create(request, user1);

        // then
        List<NotificationEntity> notificationList =
                notificationRepository.findByUserInfoIdAndMessageMessageType(
                        reviewee.getId(), REVIEW_RECEIVED);
        assertThat(notificationList).isNotEmpty();

        notificationList.forEach(
                notificationEntity -> {
                    assertThat(notificationEntity.getUserInfoId()).isEqualTo(reviewee.getId());
                    NotificationMessage message = notificationEntity.getMessage();
                    System.out.printf(
                            "title: %s, content: %s", message.getTitle(), message.getContent());
                });
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
