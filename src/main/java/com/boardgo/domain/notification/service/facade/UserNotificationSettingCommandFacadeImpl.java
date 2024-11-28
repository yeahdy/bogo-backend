package com.boardgo.domain.notification.service.facade;

import com.boardgo.domain.notification.controller.request.UserNotificationSettingUpdateRequest;
import com.boardgo.domain.notification.service.UserNotificationSettingCommandUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserNotificationSettingCommandFacadeImpl
        implements UserNotificationSettingCommandFacade {
    private final UserNotificationSettingCommandUseCase userNotificationSettingCommandUseCase;

    @Override
    public void update(Long userId, UserNotificationSettingUpdateRequest request) {
        userNotificationSettingCommandUseCase.update(
                userId, request.isAgreed(), request.messageType());
    }
}
