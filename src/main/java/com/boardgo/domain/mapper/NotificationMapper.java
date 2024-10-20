package com.boardgo.domain.mapper;

import com.boardgo.domain.notification.repository.projection.NotificationProjection;
import com.boardgo.domain.notification.repository.projection.NotificationPushProjection;
import com.boardgo.domain.notification.service.response.NotificationPushResponse;
import com.boardgo.domain.notification.service.response.NotificationResponse;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface NotificationMapper {
    NotificationMapper INSTANCE = Mappers.getMapper(NotificationMapper.class);

    List<NotificationResponse> toNotificationResponseList(
            List<NotificationProjection> notificationProjections);

    List<NotificationPushResponse> toPushResponseList(
            List<NotificationPushProjection> notificationProjections);
}
