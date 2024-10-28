package com.boardgo.domain.notification.repository;

import com.boardgo.domain.notification.entity.NotificationSettingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationSettingRepository
        extends JpaRepository<NotificationSettingEntity, Long> {}
