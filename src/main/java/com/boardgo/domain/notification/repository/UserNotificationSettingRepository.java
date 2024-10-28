package com.boardgo.domain.notification.repository;

import com.boardgo.domain.notification.entity.MessageType;
import com.boardgo.domain.notification.entity.UserNotificationSettingEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserNotificationSettingRepository
        extends JpaRepository<UserNotificationSettingEntity, Long> {
    List<UserNotificationSettingEntity> findByUserInfoId(Long userId);

    UserNotificationSettingEntity findByUserInfoIdAndNotificationSettingMessageType(
            Long userId, MessageType messageType);
}
