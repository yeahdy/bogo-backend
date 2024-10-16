package com.boardgo.domain.notification.entity;

import com.boardgo.domain.notification.service.NotificationMessageFactory;
import com.boardgo.domain.notification.service.request.NotificationCreateRequest;

public enum MessageType {
    MEETING_MODIFY,
    MEETING_REMINDER,
    REVIEW_RECEIVED,
    REQUEST_REVIEW,
    KICKED_OUT;

    public String createMessage(NotificationCreateRequest param) {
        NotificationMessageFormat messageFormat = NotificationMessageFactory.get(this);
        return switch (this) {
            case REQUEST_REVIEW -> NotificationMessageFactory.replaceMessage(
                    messageFormat.getTitle(), param);
            default -> NotificationMessageFactory.replaceMessage(messageFormat.getContent(), param);
        };
    }
}
