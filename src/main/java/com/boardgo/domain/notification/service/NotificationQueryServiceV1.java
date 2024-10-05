package com.boardgo.domain.notification.service;

import com.boardgo.domain.mapper.NotificationMapper;
import com.boardgo.domain.notification.repository.NotificationRepository;
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
        return notificationMapper.toResponseList(notificationRepository.findByUserInfoId(userId));
    }
}
