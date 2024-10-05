package com.boardgo.integration.data;

import com.boardgo.domain.notification.entity.MessageType;
import com.boardgo.domain.notification.entity.NotificationEntity;
import com.boardgo.domain.notification.entity.NotificationMessage;
import com.boardgo.domain.notification.entity.NotificationType;
import java.time.LocalDateTime;

public abstract class NotificationData {

    public static NotificationEntity.NotificationEntityBuilder getNotification(
            Long userId, NotificationMessage message) {
        return NotificationEntity.builder()
                .isRead(false)
                .userInfoId(userId)
                .pathUrl("/gatherings/1")
                .type(NotificationType.PUSH)
                .sendDateTime(LocalDateTime.of(2024, 10, 3, 18, 30))
                .message(message)
                .isSent(true);
    }

    public static NotificationMessage.NotificationMessageBuilder getNotificationMessage(
            MessageType messageType) {
        return NotificationMessage.builder()
                .title("알림 타이틀이에요!")
                .content("알림 메세지 내용이요 알림을 확인 해 주세요💌")
                .messageType(messageType);
    }
}
