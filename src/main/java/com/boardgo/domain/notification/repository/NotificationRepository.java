package com.boardgo.domain.notification.repository;

import com.boardgo.domain.notification.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository
        extends JpaRepository<NotificationEntity, Long>, NotificationDslRepository {}
