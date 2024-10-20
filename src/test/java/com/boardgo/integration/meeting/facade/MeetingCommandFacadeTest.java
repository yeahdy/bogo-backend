package com.boardgo.integration.meeting.facade;

import static com.boardgo.domain.notification.entity.MessageType.MEETING_MODIFY;
import static com.boardgo.integration.data.MeetingData.getMeetingEntityData;
import static com.boardgo.integration.data.UserInfoData.userInfoEntityData;
import static com.boardgo.integration.fixture.MeetingParticipantFixture.getParticipantMeetingParticipantEntity;
import static org.assertj.core.api.Assertions.assertThat;

import com.boardgo.domain.meeting.controller.request.MeetingUpdateRequest;
import com.boardgo.domain.meeting.entity.MeetingEntity;
import com.boardgo.domain.meeting.repository.MeetingParticipantRepository;
import com.boardgo.domain.meeting.repository.MeetingRepository;
import com.boardgo.domain.meeting.service.facade.MeetingCommandFacade;
import com.boardgo.domain.notification.entity.NotificationEntity;
import com.boardgo.domain.notification.entity.NotificationMessage;
import com.boardgo.domain.notification.repository.NotificationRepository;
import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.domain.user.repository.UserRepository;
import com.boardgo.integration.support.IntegrationTestSupport;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class MeetingCommandFacadeTest extends IntegrationTestSupport {

    @Autowired private MeetingCommandFacade meetingCommandFacade;
    @Autowired private MeetingParticipantRepository meetingParticipantRepository;
    @Autowired private MeetingRepository meetingRepository;
    @Autowired private NotificationRepository notificationRepository;
    @Autowired private UserRepository userRepository;

    @Test
    @DisplayName("모임 수정 시 모임 참여자들에게 모임 수정 알림메세지가 발송된다")
    void 모임_수정_시_모임_참여자들에게_모임_수정_알림메세지가_발송된다() {
        // given
        UserInfoEntity participant1 = userInfoEntityData("모임수정1@daum.net", "참여자1").build();
        UserInfoEntity participant2 = userInfoEntityData("모임수정2@daum.net", "참여자2").build();
        UserInfoEntity participant3 = userInfoEntityData("모임수정3@daum.net", "참여자3").build();
        userRepository.save(participant1);
        userRepository.save(participant2);
        userRepository.save(participant3);
        List<Long> userIds =
                List.of(participant1.getId(), participant2.getId(), participant3.getId());

        Long leaderId = 4L;
        MeetingEntity meeting = getMeetingEntityData(leaderId).build();
        meetingRepository.save(meeting);

        meetingParticipantRepository.save(
                getParticipantMeetingParticipantEntity(meeting.getId(), leaderId));
        meetingParticipantRepository.save(
                getParticipantMeetingParticipantEntity(meeting.getId(), participant1.getId()));
        meetingParticipantRepository.save(
                getParticipantMeetingParticipantEntity(meeting.getId(), participant2.getId()));
        meetingParticipantRepository.save(
                getParticipantMeetingParticipantEntity(meeting.getId(), participant3.getId()));

        MeetingUpdateRequest request =
                new MeetingUpdateRequest(
                        meeting.getId(),
                        "updateContent",
                        "FREE",
                        5,
                        "updatedTitle",
                        "updateCity",
                        "updateCounty",
                        "35.12321312",
                        "1232.213213213",
                        "updateAddress",
                        "updateLocation",
                        false,
                        LocalDateTime.now().plusDays(2),
                        List.of(3L, 4L));

        // when
        meetingCommandFacade.updateMeeting(request, leaderId, null);

        // then
        for (Long userId : userIds) {
            List<NotificationEntity> notificationEntities =
                    notificationRepository.findByUserInfoIdAndMessageMessageType(
                            userId, MEETING_MODIFY);
            assertThat(notificationEntities).isNotEmpty();
            notificationEntities.forEach(
                    notificationEntity -> {
                        assertThat(notificationEntity.getUserInfoId()).isEqualTo(userId);
                        NotificationMessage message = notificationEntity.getMessage();
                        assertThat(message.getTitle()).contains("모임의 정보가 변경");
                        System.out.printf(
                                "title: %s, content: %s", message.getTitle(), message.getContent());
                    });
        }
    }
}
