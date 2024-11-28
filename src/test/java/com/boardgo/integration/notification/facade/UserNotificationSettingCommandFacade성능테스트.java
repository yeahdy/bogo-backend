package com.boardgo.integration.notification.facade;

import static com.boardgo.integration.data.TermsConditionsData.getTermsConditions;
import static com.boardgo.integration.fixture.NotificationSettingFixture.getNotificationSettings;
import static org.assertj.core.api.Assertions.assertThat;

import com.boardgo.domain.notification.controller.request.UserNotificationSettingUpdateRequest;
import com.boardgo.domain.notification.entity.MessageType;
import com.boardgo.domain.notification.entity.NotificationSettingEntity;
import com.boardgo.domain.notification.entity.UserNotificationSettingEntity;
import com.boardgo.domain.notification.repository.NotificationSettingRepository;
import com.boardgo.domain.notification.repository.UserNotificationSettingRepository;
import com.boardgo.domain.notification.service.facade.UserNotificationSettingCommandFacade;
import com.boardgo.domain.termsconditions.entity.TermsConditionsEntity;
import com.boardgo.domain.termsconditions.entity.UserTermsConditionsEntity;
import com.boardgo.domain.termsconditions.entity.enums.TermsConditionsType;
import com.boardgo.domain.termsconditions.repository.TermsConditionsRepository;
import com.boardgo.domain.termsconditions.repository.UserTermsConditionsRepository;
import com.boardgo.integration.support.IntegrationTestSupport;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class UserNotificationSettingCommandFacade성능테스트 extends IntegrationTestSupport {

    @Autowired private UserNotificationSettingCommandFacade userNotificationSettingCommandFacade;
    @Autowired private UserNotificationSettingRepository userNotificationSettingRepository;
    @Autowired private NotificationSettingRepository notificationSettingRepository;
    @Autowired private UserTermsConditionsRepository userTermsConditionsRepository;
    @Autowired private TermsConditionsRepository termsConditionsRepository;
    private TermsConditionsEntity termsConditionsEntity;

    @BeforeEach
    void init() {
        CompletableFuture.runAsync(
                () -> {
                    termsConditionsEntity =
                            getTermsConditions(TermsConditionsType.PUSH).required(false).build();
                    termsConditionsRepository.save(termsConditionsEntity);
                });
    }

    @Test
    @DisplayName("회원 100명이 동시에 알림설정을 변경한다")
    void 회원_100명이_동시에_알림설정을_변경한다() throws InterruptedException {
        // given
        List<NotificationSettingEntity> notificationSettings = getNotificationSettings();
        MessageType messageType =
                notificationSettings.get(notificationSettings.size() - 1).getMessageType();
        int count = 100;
        CompletableFuture<Void> dummyFuture =
                CompletableFuture.runAsync(
                        () -> {
                            notificationSettingRepository.saveAll(notificationSettings);
                            // 회원 알림설정 하나 제외 모두 'N'
                            for (long i = 1; i <= count; i++) {
                                boolean isAgreed = Boolean.FALSE;
                                for (NotificationSettingEntity notificationSetting :
                                        notificationSettings) {
                                    if (messageType == notificationSetting.getMessageType()) {
                                        isAgreed = Boolean.TRUE;
                                    }
                                    userNotificationSettingRepository.save(
                                            UserNotificationSettingEntity.builder()
                                                    .userInfoId(i)
                                                    .notificationSetting(notificationSetting)
                                                    .isAgreed(isAgreed)
                                                    .build());
                                }
                                // 회원 푸시약관동의 활성화
                                userTermsConditionsRepository.save(
                                        UserTermsConditionsEntity.builder()
                                                .userInfoId(i)
                                                .termsConditionsEntity(termsConditionsEntity)
                                                .agreement(Boolean.TRUE)
                                                .build());
                            }
                        });
        dummyFuture.join();
        assertThat(dummyFuture.isDone()).isTrue(); // 더미데이터 추가 비동기 연산이 모두 끝날때 까지 대기
        // when
        CountDownLatch latch = new CountDownLatch(count);
        for (long i = 1; i <= count; i++) {
            long finalI = i;
            CompletableFuture<Void> future =
                    dummyFuture.thenRunAsync(
                            () -> {
                                userNotificationSettingCommandFacade.update(
                                        finalI,
                                        new UserNotificationSettingUpdateRequest(
                                                messageType, false));
                                latch.countDown();
                            });
            assertThat(future.isDone()).isFalse();
            future.join();
            assertThat(future.isCompletedExceptionally()).isFalse();
            assertThat(future.isDone()).isTrue();
        }
        // then
        boolean completed = latch.await(1L, TimeUnit.SECONDS);
        assertThat(completed).isTrue();
        assertThat(latch.getCount()).isEqualTo(0);
    }
}
