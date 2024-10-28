package com.boardgo.config;

import com.boardgo.domain.meeting.service.MeetingQueryUseCase;
import com.boardgo.domain.notification.entity.MessageType;
import com.boardgo.domain.notification.service.NotificationCommandUseCase;
import com.boardgo.domain.notification.service.facade.NotificationCommandFacade;
import com.boardgo.domain.notification.service.request.NotificationCreateRequest;
import com.boardgo.domain.user.service.UserQueryUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class NotificationCommandFacadeTestConfig {

    @Bean
    @Primary
    public NotificationCommandFacade NotificationCommandFacade() {
        return new TestNotificationCommandFacadeImpl();
    }

    @Autowired private UserQueryUseCase userQueryUseCase;
    @Autowired private MeetingQueryUseCase meetingQueryUseCase;
    @Autowired private NotificationCommandUseCase notificationCommandUseCase;

    class TestNotificationCommandFacadeImpl implements NotificationCommandFacade {

        @Override
        public void create(Long meetingId, Long userId, MessageType messageType) {
            String nickname = userQueryUseCase.getUserInfoEntity(userId).getNickName();
            String meetingTitle = meetingQueryUseCase.getMeeting(meetingId).getTitle();
            notificationCommandUseCase.createNotification(
                    messageType,
                    new NotificationCreateRequest(meetingTitle, nickname, meetingId, userId));
        }
    }
}
