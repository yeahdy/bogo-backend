package com.boardgo.domain.notification.service.facade;

import com.boardgo.domain.notification.controller.request.UserNotificationSettingUpdateRequest;

public interface UserNotificationSettingCommandFacade {
    void update(Long userId, UserNotificationSettingUpdateRequest request);
}
