package com.boardgo.domain.notification.service.facade;

import com.boardgo.domain.notification.entity.MessageType;

public interface NotificationCommandFacade {
    void create(Long meetingId, Long userId, MessageType messageType);
}
