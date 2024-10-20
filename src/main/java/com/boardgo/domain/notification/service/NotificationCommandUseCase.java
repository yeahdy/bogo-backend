package com.boardgo.domain.notification.service;

import com.boardgo.domain.notification.entity.MessageType;
import com.boardgo.domain.notification.service.request.NotificationCreateRequest;
import java.util.List;

public interface NotificationCommandUseCase {
    void readNotification(List<Long> notificationIds);

    void createNotification(MessageType messageType, NotificationCreateRequest request);

    void saveNotificationResult(Long notificationId, String result);
}
