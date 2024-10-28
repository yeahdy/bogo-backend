package com.boardgo.domain.notification.service;

public interface UserNotificationSettingCommandUseCase {
    void create(Long userId, boolean isAgreed);
}
