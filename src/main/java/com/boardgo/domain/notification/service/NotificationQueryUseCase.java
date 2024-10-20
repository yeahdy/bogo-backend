package com.boardgo.domain.notification.service;

import com.boardgo.domain.notification.entity.NotificationEntity;
import com.boardgo.domain.notification.service.response.NotificationPushResponse;
import com.boardgo.domain.notification.service.response.NotificationResponse;
import java.util.List;

public interface NotificationQueryUseCase {
    List<NotificationResponse> getNotificationList(Long userId);

    List<NotificationPushResponse> getNotificationPushList();

    NotificationEntity getNotification(Long notificationId);
}
