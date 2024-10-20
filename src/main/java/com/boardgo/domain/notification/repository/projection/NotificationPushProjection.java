package com.boardgo.domain.notification.repository.projection;

public record NotificationPushProjection(
        String token, String title, String content, String pathUrl, Long notificationId) {}
