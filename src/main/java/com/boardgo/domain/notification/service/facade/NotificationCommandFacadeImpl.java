package com.boardgo.domain.notification.service.facade;

import com.boardgo.domain.meeting.service.MeetingQueryUseCase;
import com.boardgo.domain.notification.entity.MessageType;
import com.boardgo.domain.notification.service.NotificationCommandUseCase;
import com.boardgo.domain.notification.service.UserNotificationSettingQueryUseCase;
import com.boardgo.domain.notification.service.request.NotificationCreateRequest;
import com.boardgo.domain.termsconditions.entity.enums.TermsConditionsType;
import com.boardgo.domain.termsconditions.service.UserTermsConditionsQueryUseCase;
import com.boardgo.domain.user.service.UserQueryUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationCommandFacadeImpl implements NotificationCommandFacade {

    private final NotificationCommandUseCase notificationCommandUseCase;
    private final UserTermsConditionsQueryUseCase userTermsConditionsQueryUseCase;
    private final UserNotificationSettingQueryUseCase userNotificationSettingQueryUseCase;
    private final UserQueryUseCase userQueryUseCase;
    private final MeetingQueryUseCase meetingQueryUseCase;

    @Override
    public void create(Long meetingId, Long userId, MessageType messageType) {
        // 1. 회원의 푸시약관동의가 비허용이면 발송x
        if (!userTermsConditionsQueryUseCase
                .getUserTermsConditionsEntity(userId, TermsConditionsType.PUSH)
                .getAgreement()) {
            return;
        }
        // 2. 회원의 알림설정 중 messageType 이 비활성화일 경우 발송X
        if (!userNotificationSettingQueryUseCase
                .getUserNotificationSetting(userId, messageType)
                .getIsAgreed()) {
            return;
        }
        String nickname = userQueryUseCase.getUserInfoEntity(userId).getNickName();
        String meetingTitle = meetingQueryUseCase.getMeeting(meetingId).getTitle();
        notificationCommandUseCase.createNotification(
                messageType,
                new NotificationCreateRequest(meetingTitle, nickname, meetingId, userId));
    }
}
