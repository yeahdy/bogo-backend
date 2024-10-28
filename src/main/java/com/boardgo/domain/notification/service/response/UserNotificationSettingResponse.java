package com.boardgo.domain.notification.service.response;

import com.boardgo.domain.notification.entity.MessageType;

public record UserNotificationSettingResponse(
        Boolean isAgreed, String content, String additionalContent, MessageType messageType) {}
