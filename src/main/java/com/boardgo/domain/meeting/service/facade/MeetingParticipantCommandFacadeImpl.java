package com.boardgo.domain.meeting.service.facade;

import static com.boardgo.domain.notification.entity.MessageType.KICKED_OUT;

import com.boardgo.domain.meeting.service.MeetingParticipantCommandUseCase;
import com.boardgo.domain.notification.service.NotificationCommandUseCase;
import com.boardgo.domain.notification.service.request.NotificationCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MeetingParticipantCommandFacadeImpl implements MeetingParticipantCommandFacade {
    private final MeetingParticipantCommandUseCase meetingParticipantCommandUseCase;
    private final NotificationCommandUseCase notificationCommandUseCase;

    @Override
    public void outMeeting(Long meetingId, Long userId, boolean isKicked) {
        meetingParticipantCommandUseCase.outMeeting(meetingId, userId);
        if (isKicked) {
            notificationCommandUseCase.createNotification(
                    userId, KICKED_OUT, new NotificationCreateRequest("알림제목", "닉네임", null));
        }
    }
}
