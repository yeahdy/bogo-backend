package com.boardgo.domain.notification.service;

import com.boardgo.domain.notification.entity.MessageType;
import com.boardgo.domain.notification.entity.UserNotificationSettingEntity;
import com.boardgo.domain.notification.service.response.UserNotificationSettingResponse;
import java.util.List;

public interface UserNotificationSettingQueryUseCase {
    List<UserNotificationSettingResponse> getUserNotificationSettingsList(Long userId);

    UserNotificationSettingEntity getUserNotificationSetting(Long userId, MessageType messageType);
}
