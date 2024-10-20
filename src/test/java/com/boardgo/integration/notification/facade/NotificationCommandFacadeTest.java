package com.boardgo.integration.notification.facade;

import static com.boardgo.integration.data.MeetingData.getMeetingEntityData;
import static com.boardgo.integration.data.UserInfoData.userInfoEntityData;
import static org.assertj.core.api.Assertions.assertThat;

import com.boardgo.domain.meeting.entity.MeetingEntity;
import com.boardgo.domain.meeting.repository.MeetingRepository;
import com.boardgo.domain.notification.entity.MessageType;
import com.boardgo.domain.notification.entity.NotificationEntity;
import com.boardgo.domain.notification.entity.NotificationMessage;
import com.boardgo.domain.notification.repository.NotificationRepository;
import com.boardgo.domain.notification.service.facade.NotificationCommandFacade;
import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.domain.user.repository.UserRepository;
import com.boardgo.integration.support.IntegrationTestSupport;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;

public class NotificationCommandFacadeTest extends IntegrationTestSupport {

    @Autowired private NotificationCommandFacade notificationCommandFacade;
    @Autowired private MeetingRepository meetingRepository;
    @Autowired private NotificationRepository notificationRepository;
    @Autowired private UserRepository userRepository;

    @ParameterizedTest
    @DisplayName("알림 메세지 타입에 따라 알림메세지를 만들 수 있다")
    @EnumSource(MessageType.class)
    void 알림_메세지_타입에_따라_알림메세지를_만들_수_있다(MessageType messageType) {
        // given
        UserInfoEntity participant = userInfoEntityData("user1@daum.net", "사용자1").build();
        userRepository.save(participant);

        Long leaderId = 2L;
        MeetingEntity meeting = getMeetingEntityData(leaderId).build();
        meetingRepository.save(meeting);

        // when
        notificationCommandFacade.create(participant.getId(), meeting.getId(), messageType);

        // then
        List<NotificationEntity> notificationEntities =
                notificationRepository.findByUserInfoIdAndMessageMessageType(
                        participant.getId(), messageType);
        assertThat(notificationEntities).isNotEmpty();

        notificationEntities.forEach(
                notificationEntity -> {
                    assertThat(notificationEntity.getUserInfoId()).isEqualTo(participant.getId());
                    NotificationMessage message = notificationEntity.getMessage();
                    assertThat(message.getMessageType()).isEqualTo(messageType);
                    System.out.printf(
                            "title: %s, content: %s", message.getTitle(), message.getContent());
                });
    }
}
