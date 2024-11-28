package com.boardgo.domain.notification.service;

import com.boardgo.domain.notification.entity.MessageType;

public interface UserNotificationSettingCommandUseCase {
    void create(Long userId, boolean isAgreed);

    void update(Long userId, boolean isAgreed, MessageType messageType);
}
