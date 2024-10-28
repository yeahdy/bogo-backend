package com.boardgo.integration.notification.service;

import static com.boardgo.integration.data.UserInfoData.userInfoEntityData;
import static com.boardgo.integration.fixture.NotificationSettingFixture.getNotificationSettings;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.boardgo.common.exception.CustomIllegalArgumentException;
import com.boardgo.domain.notification.entity.MessageType;
import com.boardgo.domain.notification.entity.NotificationSettingEntity;
import com.boardgo.domain.notification.entity.UserNotificationSettingEntity;
import com.boardgo.domain.notification.repository.NotificationSettingRepository;
import com.boardgo.domain.notification.repository.UserNotificationSettingRepository;
import com.boardgo.domain.notification.service.UserNotificationSettingQueryUseCase;
import com.boardgo.domain.notification.service.response.UserNotificationSettingResponse;
import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.domain.user.repository.UserRepository;
import com.boardgo.integration.support.IntegrationTestSupport;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;

public class UserNotificationSettingQueryServiceV1Test extends IntegrationTestSupport {

    @Autowired UserNotificationSettingQueryUseCase userNotificationSettingQueryUseCase;
    @Autowired UserRepository userRepository;
    @Autowired NotificationSettingRepository notificationSettingRepository;
    @Autowired UserNotificationSettingRepository userNotificationSettingRepository;
    private final List<NotificationSettingEntity> notificationSettings = getNotificationSettings();

    @BeforeEach
    void init() {
        notificationSettingRepository.saveAll(notificationSettings);
    }

    @Test
    @DisplayName("회원의 알림설정 조회하기")
    void 회원의_알림설정_조회하기() {
        // given
        UserInfoEntity user = userInfoEntityData("user1@naver.com", "user1").build();
        userRepository.save(user);

        notificationSettings.forEach(
                notificationSettingEntity -> {
                    userNotificationSettingRepository.save(
                            UserNotificationSettingEntity.builder()
                                    .userInfoId(user.getId())
                                    .notificationSetting(notificationSettingEntity)
                                    .isAgreed(true)
                                    .build());
                });

        // when
        List<UserNotificationSettingResponse> userNotificationSettingsList =
                userNotificationSettingQueryUseCase.getUserNotificationSettingsList(user.getId());

        // then
        assertThat(userNotificationSettingsList).isNotEmpty();
        List<MessageType> messageTypes = Arrays.stream(MessageType.values()).toList();

        userNotificationSettingsList.forEach(
                response -> {
                    assertThat(response.isAgreed()).isNotNull().isTrue();
                    assertThat(response.content()).isNotNull();
                    assertThat(response.additionalContent()).isNotNull();
                    assertThat(messageTypes.contains(response.messageType())).isTrue();
                });
    }

    @Test
    @DisplayName("회원의 알림설정 목록이 존재하지 않으면 예외가 발생한다")
    void 회원의_알림설정_목록이_존재하지_않으면_예외가_발생한다() {
        // given
        UserInfoEntity user = userInfoEntityData("user1@naver.com", "user1").build();
        userRepository.save(user);

        // when
        // then
        assertThatThrownBy(
                        () ->
                                userNotificationSettingQueryUseCase.getUserNotificationSettingsList(
                                        user.getId()))
                .isInstanceOf(CustomIllegalArgumentException.class)
                .hasMessageContaining("회원의 알림설정이 존재하지 않습니다");
    }

    @ParameterizedTest
    @DisplayName("알림 항목 별로 회원의 알림설정 활성화 유무를 확인할 수 있다")
    @EnumSource(MessageType.class)
    void 알림_항목_별로_회원의_알림설정_활성화_유무를_확인할_수_있다(MessageType messageType) {
        // given
        UserInfoEntity user = userInfoEntityData("user1@naver.com", "user1").build();
        userRepository.save(user);

        notificationSettings.forEach(
                notificationSettingEntity -> {
                    userNotificationSettingRepository.save(
                            UserNotificationSettingEntity.builder()
                                    .userInfoId(user.getId())
                                    .notificationSetting(notificationSettingEntity)
                                    .isAgreed(true)
                                    .build());
                });

        // when
        UserNotificationSettingEntity userNotificationSetting =
                userNotificationSettingQueryUseCase.getUserNotificationSetting(
                        user.getId(), messageType);

        // then
        assertThat(userNotificationSetting.getNotificationSetting().getMessageType())
                .isEqualTo(messageType);
        assertThat(userNotificationSetting.getIsAgreed()).isNotNull().isInstanceOf(Boolean.class);
    }
}
