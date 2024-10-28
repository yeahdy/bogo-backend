package com.boardgo.integration.notification.service;

import static com.boardgo.integration.fixture.NotificationSettingFixture.getNotificationSettings;
import static org.assertj.core.api.Assertions.assertThat;

import com.boardgo.domain.notification.entity.MessageType;
import com.boardgo.domain.notification.entity.NotificationSettingEntity;
import com.boardgo.domain.notification.entity.UserNotificationSettingEntity;
import com.boardgo.domain.notification.repository.NotificationSettingRepository;
import com.boardgo.domain.notification.repository.UserNotificationSettingRepository;
import com.boardgo.domain.notification.service.UserNotificationSettingCommandUseCase;
import com.boardgo.integration.support.IntegrationTestSupport;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class UserNotificationSettingCommandServiceV1Test extends IntegrationTestSupport {

    @Autowired private UserNotificationSettingCommandUseCase userNotificationSettingCommandUseCase;
    @Autowired private UserNotificationSettingRepository userNotificationSettingRepository;
    @Autowired private NotificationSettingRepository notificationSettingRepository;

    @BeforeEach
    void init() {
        notificationSettingRepository.saveAll(getNotificationSettings());
    }

    @Test
    @DisplayName("회원의 알림설정 항목을 일괄 저장한다")
    void 회원의_알림설정_항목을_일괄_저장한다() {
        // given
        Long userId = 1L;
        boolean isAgreed = true;

        // when
        userNotificationSettingCommandUseCase.create(userId, isAgreed);

        // then
        List<UserNotificationSettingEntity> userNotificationSettingEntities =
                userNotificationSettingRepository.findByUserInfoId(userId);
        assertThat(userNotificationSettingEntities).isNotEmpty();

        List<MessageType> messageTypeList = Arrays.stream(MessageType.values()).toList();
        assertThat(messageTypeList.size()).isEqualTo(userNotificationSettingEntities.size());

        userNotificationSettingEntities.forEach(
                userNotificationSetting -> {
                    assertThat(userNotificationSetting.getUserInfoId()).isEqualTo(userId);
                    assertThat(userNotificationSetting.getIsAgreed()).isEqualTo(isAgreed);
                    NotificationSettingEntity notificationSetting =
                            userNotificationSetting.getNotificationSetting();
                    assertThat(messageTypeList.contains(notificationSetting.getMessageType()))
                            .isTrue();
                });
    }
}
