package com.boardgo.config;

import static org.mockito.Mockito.mock;

import com.boardgo.domain.notification.service.UserNotificationSettingCommandUseCase;
import com.boardgo.domain.notification.service.UserNotificationSettingQueryUseCase;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class UserNotificationSettingCommandFacadeTestConfig {

    @Bean
    @Primary
    public UserNotificationSettingCommandUseCase userNotificationSettingCommandUseCase() {
        return mock(UserNotificationSettingCommandUseCase.class);
    }

    @Bean
    @Primary
    public UserNotificationSettingQueryUseCase userNotificationSettingQueryUseCase() {
        return mock(UserNotificationSettingQueryUseCase.class);
    }
}
