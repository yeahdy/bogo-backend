package com.boardgo.domain.notification.repository;

import com.boardgo.domain.notification.entity.MessageType;
import com.boardgo.domain.notification.entity.NotificationSettingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationSettingRepository
        extends JpaRepository<NotificationSettingEntity, Long> {
    NotificationSettingEntity findByMessageType(MessageType messageType);
}
