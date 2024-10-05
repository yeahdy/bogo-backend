package com.boardgo.domain.notification.service.response;

public record NotificationResponse(
        Long notificationId, String title, String content, Boolean isRead, String pathUrl) {}
