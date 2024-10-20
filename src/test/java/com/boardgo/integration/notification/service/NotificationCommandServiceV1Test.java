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
import com.boardgo.domain.notification.service.request.NotificationCreateRequest;
import com.boardgo.integration.support.IntegrationTestSupport;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
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

    @ParameterizedTest
    @DisplayName("알림 메세지 타입별로 페이지 이동 경로가 다르다")
    @EnumSource(MessageType.class)
    void 알림_메세지_타입별로_페이지_이동_경로가_다르다(MessageType messageType) {
        // given
        Long userId = 1L;
        Long meetingId = null;
        String pathUrl = "";
        switch (messageType) {
            case MEETING_MODIFY, MEETING_REMINDER -> {
                meetingId = 1L;
                pathUrl = "/gatherings/" + meetingId;
            }
            case REVIEW_RECEIVED -> pathUrl = "/mypage/review/receivedReviews";
            case REQUEST_REVIEW -> pathUrl = "/mypage/review";
            case KICKED_OUT -> pathUrl = "/gatherings";
        }
        NotificationCreateRequest request = new NotificationCreateRequest("알림제목", "닉네임", meetingId);

        // when
        notificationCommandUseCase.createNotification(userId, messageType, request);

        // then
        List<NotificationEntity> notificationEntities =
                notificationRepository.findByUserInfoIdAndMessageMessageType(userId, messageType);
        assertThat(notificationEntities).isNotEmpty();
        String finalPathUrl = pathUrl;
        notificationEntities.forEach(
                notification -> {
                    assertThat(notification.getPathUrl()).isEqualTo(finalPathUrl);
                    assertThat(notification.getMessage().getMessageType()).isEqualTo(messageType);
                });
    }

    @Test
    @DisplayName("알림 푸시 결과를 저장하고 알림 전송 상태를 변경한다")
    void 알림_푸시_결과를_저장하고_알림_전송_상태를_변경한다() {
        // given
        NotificationEntity notification =
                getNotification(1L, getNotificationMessage(MessageType.MEETING_MODIFY).build())
                        .isSent(false)
                        .build();
        notificationRepository.save(notification);
        String result = "success push message result";

        // when
        notificationCommandUseCase.saveNotificationResult(notification.getId(), result);

        // then
        NotificationEntity notificationEntity =
                notificationRepository.findById(notification.getId()).get();
        assertThat(notificationEntity).isNotNull();
        assertThat(notificationEntity.getIsSent()).isTrue();
        assertThat(notificationEntity.getRawResult()).isEqualTo(result);
    }
}
