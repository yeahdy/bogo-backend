package com.boardgo.domain.notification.repository;

import com.boardgo.domain.notification.entity.MessageType;
import com.boardgo.domain.notification.entity.NotificationEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository
        extends JpaRepository<NotificationEntity, Long>, NotificationDslRepository {
    List<NotificationEntity> findAllByIdInAndIsReadAndIsSent(
            List<Long> id, Boolean isRead, Boolean isSent);

    @Modifying
    @Query("UPDATE NotificationEntity n " + "SET n.isRead = 'Y' " + "WHERE n.id IN :ids")
    void readNotifications(@Param("ids") List<Long> ids);

    List<NotificationEntity> findByUserInfoIdAndMessageMessageType(
            Long userId, MessageType messageType);
}
