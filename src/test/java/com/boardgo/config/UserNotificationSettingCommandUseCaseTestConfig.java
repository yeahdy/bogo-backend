package com.boardgo.config;

import com.boardgo.domain.notification.entity.MessageType;
import com.boardgo.domain.notification.service.UserNotificationSettingCommandUseCase;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class UserNotificationSettingCommandUseCaseTestConfig {

    @Bean
    @Primary
    public UserNotificationSettingCommandUseCase userNotificationSettingCommandUseCase() {
        return new TestUserNotificationSettingCommandService();
    }

    static class TestUserNotificationSettingCommandService
            implements UserNotificationSettingCommandUseCase {
        @Override
        public void create(Long userId, boolean isAgreed) {}

        @Override
        public void update(Long userId, boolean isAgreed, MessageType messageType) {}
    }
}
