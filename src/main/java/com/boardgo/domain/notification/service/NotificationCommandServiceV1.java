package com.boardgo.domain.notification.service;

import static com.boardgo.domain.notification.entity.NotificationType.PUSH;

import com.boardgo.common.exception.CustomNoSuchElementException;
import com.boardgo.domain.notification.entity.MessageType;
import com.boardgo.domain.notification.entity.NotificationEntity;
import com.boardgo.domain.notification.entity.NotificationMessage;
import com.boardgo.domain.notification.repository.NotificationRepository;
import com.boardgo.domain.notification.service.request.NotificationCreateRequest;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationCommandServiceV1 implements NotificationCommandUseCase {
    private final NotificationRepository notificationRepository;
    private final String MEETING_URL = "/gatherings";
    private final String REVIEW_RECEIVED_URL = "/mypage/review/receivedReviews";
    private final String REQUEST_REVIEW_URL = "/mypage/review";

    @Override
    public void readNotification(List<Long> notificationIds) {
        List<NotificationEntity> notificationEntity =
                notificationRepository.findAllByIdInAndIsReadAndIsSent(
                        notificationIds, false, true);
        if (notificationEntity.isEmpty()) {
            throw new CustomNoSuchElementException("알림");
        }
        List<Long> ids = notificationEntity.stream().map(NotificationEntity::getId).toList();
        notificationRepository.readNotifications(ids);
    }

    @Override
    public void createNotification(
            Long userId, MessageType messageType, NotificationCreateRequest request) {
        String pathUrl = "";
        String title = NotificationMessageFactory.get(messageType).getTitle();
        String content = messageType.createMessage(request);

        switch (messageType) {
            case MEETING_MODIFY, MEETING_REMINDER -> pathUrl =
                    MEETING_URL + "/" + request.meetingId();
            case REVIEW_RECEIVED -> pathUrl = REVIEW_RECEIVED_URL;
            case REQUEST_REVIEW -> {
                title = messageType.createMessage(request);
                content = NotificationMessageFactory.get(messageType).getContent();
                pathUrl = REQUEST_REVIEW_URL;
            }
            case KICKED_OUT -> pathUrl = MEETING_URL;
        }

        notificationRepository.save(
                NotificationEntity.builder()
                        .userInfoId(userId)
                        .pathUrl(pathUrl)
                        .type(PUSH)
                        .message(setNotificationMessage(messageType, title, content))
                        .sendDateTime(LocalDateTime.now())
                        .build());
    }

    private NotificationMessage setNotificationMessage(
            MessageType messageType, String title, String content) {
        return NotificationMessage.builder()
                .messageType(messageType)
                .title(title)
                .content(content)
                .build();
    }
}
