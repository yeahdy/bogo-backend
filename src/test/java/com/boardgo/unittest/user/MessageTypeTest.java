package com.boardgo.unittest.user;

import static com.boardgo.domain.notification.entity.MessageType.REQUEST_REVIEW;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

import com.boardgo.domain.notification.entity.MessageType;
import com.boardgo.domain.notification.entity.NotificationMessageFormat;
import com.boardgo.domain.notification.service.NotificationMessageFactory;
import com.boardgo.domain.notification.service.request.NotificationCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MessageTypeTest {

    @ParameterizedTest
    @DisplayName("알림 메세지를 치환할 수 있다")
    @EnumSource(MessageType.class)
    void 알림_메세지를_치환할_수_있다(MessageType messageType) {
        // given
        String title = "알림메세지 {#meetingTitle}";
        String content = "{#nickName} 님의 알림메세지 내용";
        String meetingTitle = "보드게임 같이 하실분!";
        String nickname = "행복전도사";
        NotificationCreateRequest param =
                new NotificationCreateRequest(meetingTitle, nickname, 1L, 1L);

        try (MockedStatic<NotificationMessageFactory> notificationMessageFactory =
                mockStatic(NotificationMessageFactory.class)) {
            NotificationMessageFormat notificationMessageFormat =
                    NotificationMessageFormat.builder()
                            .title(title)
                            .content(content)
                            .messageType(messageType)
                            .build();

            given(NotificationMessageFactory.get(messageType))
                    .willReturn(notificationMessageFormat);
            if (REQUEST_REVIEW == messageType) {
                given(NotificationMessageFactory.replaceMessage(title, param))
                        .willReturn("알림메세지 " + meetingTitle);
            } else {
                given(NotificationMessageFactory.replaceMessage(content, param))
                        .willReturn(nickname + " 님의 알림메세지 내용");
            }

            // when
            String message = messageType.createMessage(param);

            // then
            if (REQUEST_REVIEW == messageType) {
                assertThat(message).contains(meetingTitle);
            } else {
                assertThat(message).contains(nickname);
            }
        }
    }
}
