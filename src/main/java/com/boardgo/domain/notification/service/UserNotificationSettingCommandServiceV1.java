package com.boardgo.domain.notification.service;

import com.boardgo.domain.notification.entity.MessageType;
import com.boardgo.domain.notification.entity.UserNotificationSettingEntity;
import com.boardgo.domain.notification.repository.NotificationSettingRepository;
import com.boardgo.domain.notification.repository.UserNotificationSettingRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserNotificationSettingCommandServiceV1
        implements UserNotificationSettingCommandUseCase {

    private final UserNotificationSettingRepository userNotificationSettingRepository;
    private final NotificationSettingRepository notificationSettingRepository;

    @Override
    public void create(Long userId, boolean isAgreed) {
        List<UserNotificationSettingEntity> notificationSettingEntities = new ArrayList<>();
        for (MessageType messageType : MessageType.values()) {
            notificationSettingEntities.add(
                    UserNotificationSettingEntity.builder()
                            .userInfoId(userId)
                            .notificationSetting(
                                    notificationSettingRepository.findByMessageType(messageType))
                            .isAgreed(isAgreed)
                            .build());
        }
        userNotificationSettingRepository.saveAll(notificationSettingEntities);
    }
}
