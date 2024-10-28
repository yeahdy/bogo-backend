package com.boardgo.integration.fixture;

import com.boardgo.domain.notification.entity.MessageType;
import com.boardgo.domain.notification.entity.NotificationSettingEntity;
import java.util.ArrayList;
import java.util.List;

public abstract class NotificationSettingFixture {
    public static List<NotificationSettingEntity> getNotificationSettings() {
        List<NotificationSettingEntity> notificationSettingEntities = new ArrayList<>();
        for (MessageType messageType : MessageType.values()) {
            notificationSettingEntities.add(
                    NotificationSettingEntity.builder()
                            .messageType(messageType)
                            .content("알림 내용이에요")
                            .additionalContent("알림에 대한 부가 설명이에요")
                            .build());
        }
        return notificationSettingEntities;
    }
}
