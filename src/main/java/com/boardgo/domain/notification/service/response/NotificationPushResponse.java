package com.boardgo.domain.notification.service.response;

public record NotificationPushResponse(
        String token, String title, String content, String pathUrl, Long notificationId) {}
