package com.boardgo.unittest.notification;

import static org.assertj.core.api.Assertions.assertThat;

import com.boardgo.domain.notification.entity.MessageType;
import com.boardgo.domain.notification.entity.NotificationEntity;
import com.boardgo.domain.notification.entity.NotificationMessage;
import com.boardgo.domain.notification.entity.NotificationType;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class NotificationEntityTest {
    @Test
    @DisplayName("알림 메세지를 읽으면 값이 true 가 된다")
    void 알림_메세지를_읽으면_값이_true_가_된다() {
        // given
        NotificationEntity notificationEntity =
                NotificationEntity.builder()
                        .isRead(false)
                        .userInfoId(1L)
                        .pathUrl("/test")
                        .type(NotificationType.PUSH)
                        .sendDateTime(LocalDateTime.of(2023, 10, 3, 18, 0))
                        .message(
                                NotificationMessage.builder()
                                        .title("알림메세지 제목입니다")
                                        .content("알림메세지 내용입니다")
                                        .messageType(MessageType.REVIEW_RECEIVED)
                                        .build())
                        .isSent(true)
                        .build();

        // when
        notificationEntity.read();

        // then
        assertThat(notificationEntity.getIsRead()).isTrue();
    }
}
