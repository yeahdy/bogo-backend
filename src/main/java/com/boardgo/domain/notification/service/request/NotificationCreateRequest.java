package com.boardgo.domain.notification.service.request;

public record NotificationCreateRequest(
        String meetingTitle, String nickname, Long meetingId, Long userId) {}
