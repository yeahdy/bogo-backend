package com.boardgo.integration.meeting.facade;

import static com.boardgo.domain.notification.entity.MessageType.KICKED_OUT;
import static com.boardgo.integration.data.MeetingData.getMeetingEntityData;
import static com.boardgo.integration.data.UserInfoData.userInfoEntityData;
import static com.boardgo.integration.fixture.MeetingParticipantFixture.getParticipantMeetingParticipantEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.boardgo.common.exception.CustomIllegalArgumentException;
import com.boardgo.domain.meeting.entity.MeetingEntity;
import com.boardgo.domain.meeting.repository.MeetingParticipantRepository;
import com.boardgo.domain.meeting.repository.MeetingRepository;
import com.boardgo.domain.meeting.service.facade.MeetingParticipantCommandFacade;
import com.boardgo.domain.notification.entity.NotificationEntity;
import com.boardgo.domain.notification.entity.NotificationMessage;
import com.boardgo.domain.notification.repository.NotificationRepository;
import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.domain.user.repository.UserRepository;
import com.boardgo.integration.support.IntegrationTestSupport;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class MeetingParticipantCommandFacadeTest extends IntegrationTestSupport {
    @Autowired private MeetingParticipantCommandFacade meetingParticipantCommandFacade;
    @Autowired private MeetingParticipantRepository meetingParticipantRepository;
    @Autowired private MeetingRepository meetingRepository;
    @Autowired private NotificationRepository notificationRepository;
    @Autowired private UserRepository userRepository;

    @Test
    @DisplayName("모임 내보내기 시 모임을 나가는 도중 예외가 발생할 경우 알림메세지를 저장하지 않는다")
    void 모임_내보내기_시_모임을_나가는_도중_예외가_발생할_경우_알림메세지를_저장하지_않는다() {
        // given
        UserInfoEntity notpParticipant = userInfoEntityData("out@daum.net", "나는강퇴자").build();
        userRepository.save(notpParticipant);

        Long leaderId = 2L;
        MeetingEntity meeting = getMeetingEntityData(leaderId).build();
        meetingRepository.save(meeting);
        meetingParticipantRepository.save(
                getParticipantMeetingParticipantEntity(meeting.getId(), leaderId));

        // when
        assertThrows(
                CustomIllegalArgumentException.class,
                () ->
                        meetingParticipantCommandFacade.outMeeting(
                                meeting.getId(), notpParticipant.getId(), true));

        // then
        assertThat(
                        notificationRepository.findByUserInfoIdAndMessageMessageType(
                                notpParticipant.getId(), KICKED_OUT))
                .isEmpty();
    }

    @Test
    @DisplayName("모임 내보내기를 할 경우 강퇴당한 사용자에게 알림메세지가 발송된다")
    void 모임_내보내기를_할_경우_강퇴당한_사용자에게_알림메세지가_발송된다() {
        // given
        UserInfoEntity participant = userInfoEntityData("강퇴당해요@daum.net", "나는참여자").build();
        userRepository.save(participant);

        Long leaderId = 2L;
        MeetingEntity meeting = getMeetingEntityData(leaderId).build();
        meetingRepository.save(meeting);

        meetingParticipantRepository.save(
                getParticipantMeetingParticipantEntity(meeting.getId(), leaderId));
        meetingParticipantRepository.save(
                getParticipantMeetingParticipantEntity(meeting.getId(), participant.getId()));

        // when
        meetingParticipantCommandFacade.outMeeting(meeting.getId(), participant.getId(), true);

        // then
        List<NotificationEntity> notificationEntities =
                notificationRepository.findByUserInfoIdAndMessageMessageType(
                        participant.getId(), KICKED_OUT);
        assertThat(notificationEntities).isNotEmpty();

        notificationEntities.forEach(
                notificationEntity -> {
                    assertThat(notificationEntity.getUserInfoId()).isEqualTo(participant.getId());
                    NotificationMessage message = notificationEntity.getMessage();
                    assertThat(message.getTitle()).contains("퇴장");
                    System.out.printf(
                            "title: %s, content: %s", message.getTitle(), message.getContent());
                });
    }
}
