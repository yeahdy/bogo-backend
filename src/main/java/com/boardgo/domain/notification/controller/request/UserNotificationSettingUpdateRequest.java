package com.boardgo.domain.notification.controller.request;

import com.boardgo.domain.notification.entity.MessageType;

public record UserNotificationSettingUpdateRequest(MessageType messageType, Boolean isAgreed) {}
