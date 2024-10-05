package com.boardgo.domain.notification.repository.projection;

public record NotificationProjection(
        Long notificationId, String title, String content, Boolean isRead, String pathUrl) {}
