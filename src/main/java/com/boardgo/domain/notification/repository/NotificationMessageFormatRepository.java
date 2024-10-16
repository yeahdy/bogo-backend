package com.boardgo.domain.notification.repository;

import com.boardgo.domain.notification.entity.NotificationMessageFormat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationMessageFormatRepository
        extends JpaRepository<NotificationMessageFormat, Long> {}
