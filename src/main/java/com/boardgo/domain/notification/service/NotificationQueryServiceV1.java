package com.boardgo.domain.notification.service;

import com.boardgo.common.exception.CustomNullPointException;
import com.boardgo.domain.mapper.NotificationMapper;
import com.boardgo.domain.notification.entity.NotificationEntity;
import com.boardgo.domain.notification.repository.NotificationRepository;
import com.boardgo.domain.notification.service.response.NotificationPushResponse;
import com.boardgo.domain.notification.service.response.NotificationResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationQueryServiceV1 implements NotificationQueryUseCase {
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Override
    public List<NotificationResponse> getNotificationList(Long userId) {
        return notificationMapper.toNotificationResponseList(
                notificationRepository.findByUserInfoId(userId));
    }

    @Override
    public List<NotificationPushResponse> getNotificationPushList() {
        return notificationMapper.toPushResponseList(notificationRepository.findNotificationPush());
    }

    @Override
    public NotificationEntity getNotification(Long notificationId) {
        return notificationRepository
                .findById(notificationId)
                .orElseThrow(() -> new CustomNullPointException("알림이 존재하지 않습니다"));
    }
}
