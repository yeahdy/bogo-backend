package com.boardgo.integration.notification.service;

import static com.boardgo.integration.data.NotificationData.getNotification;
import static com.boardgo.integration.data.NotificationData.getNotificationMessage;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.boardgo.common.exception.CustomNoSuchElementException;
import com.boardgo.domain.notification.entity.MessageType;
import com.boardgo.domain.notification.entity.NotificationEntity;
import com.boardgo.domain.notification.entity.NotificationMessage;
import com.boardgo.domain.notification.repository.NotificationRepository;
import com.boardgo.domain.notification.service.NotificationCommandUseCase;
import com.boardgo.integration.support.IntegrationTestSupport;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class NotificationCommandServiceV1Test extends IntegrationTestSupport {
    @Autowired private NotificationRepository notificationRepository;
    @Autowired private NotificationCommandUseCase notificationCommandUseCase;
    @Autowired private EntityManager em;

    @Test
    @DisplayName("발송한 알림메세지만 읽을 수 있다")
    void 발송한_알림메세지만_읽을_수_있다() {
        // given
        NotificationMessage message = getNotificationMessage(MessageType.MEETING_REMINDER).build();
        List<Long> notificationIds = new ArrayList<>();
        // 발송
        for (int i = 0; i < 5; i++) {
            NotificationEntity notificationEntity =
                    notificationRepository.save(
                            getNotification(1L, message).pathUrl("/gatherings/" + i + 1).build());
            notificationIds.add(notificationEntity.getId());
        }
        // 미발송
        for (int i = 0; i < 5; i++) {
            NotificationEntity notificationEntity =
                    notificationRepository.save(
                            getNotification(1L, message)
                                    .isSent(false)
                                    .pathUrl("/gatherings/" + i + 1)
                                    .build());
            notificationIds.add(notificationEntity.getId());
        }

        // when
        notificationCommandUseCase.readNotification(notificationIds);

        // then
        em.flush();
        em.clear();
        List<NotificationEntity> notificationEntities =
                notificationRepository.findAllById(notificationIds);
        assertThat(notificationEntities).isNotEmpty();

        notificationEntities.forEach(
                notification -> {
                    if (notification.getIsSent()) {
                        assertThat(notification.getIsRead()).isTrue();
                    } else {
                        assertThat(notification.getIsRead()).isFalse();
                    }
                });
    }

    @Test
    @DisplayName("유효하지 않은 알림메세지일 경우 예외가 발생한다")
    void 유효하지_않은_알림메세지일_경우_예외가_발생한다() {
        // given
        List<Long> notificationIds = new ArrayList<>();
        notificationIds.add(100L);
        notificationIds.add(101L);
        notificationIds.add(102L);

        // when
        // then
        assertThatThrownBy(() -> notificationCommandUseCase.readNotification(notificationIds))
                .isInstanceOf(CustomNoSuchElementException.class)
                .hasMessageContaining("알림 (이)가 존재하지 않습니다");
    }
}
