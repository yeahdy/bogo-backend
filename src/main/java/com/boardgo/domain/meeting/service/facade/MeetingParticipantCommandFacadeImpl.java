package com.boardgo.domain.meeting.service.facade;

import static com.boardgo.domain.meeting.entity.enums.MeetingState.COMPLETE;
import static com.boardgo.domain.meeting.entity.enums.MeetingState.PROGRESS;
import static com.boardgo.domain.notification.entity.MessageType.KICKED_OUT;

import com.boardgo.domain.meeting.entity.MeetingEntity;
import com.boardgo.domain.meeting.service.MeetingParticipantCommandUseCase;
import com.boardgo.domain.meeting.service.MeetingQueryUseCase;
import com.boardgo.domain.notification.service.facade.NotificationCommandFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MeetingParticipantCommandFacadeImpl implements MeetingParticipantCommandFacade {
    private final MeetingParticipantCommandUseCase meetingParticipantCommandUseCase;
    private final MeetingQueryUseCase meetingQueryUseCase;
    private final NotificationCommandFacade notificationCommandFacade;

    @Override
    public void outMeeting(Long meetingId, Long userId, boolean isKicked) {
        meetingParticipantCommandUseCase.outMeeting(meetingId, userId);
        MeetingEntity meeting = meetingQueryUseCase.getMeeting(meetingId);
        if (COMPLETE.equals(meeting.getState())) {
            meeting.updateMeetingState(PROGRESS);
        }
        if (isKicked) {
            notificationCommandFacade.create(meetingId, userId, KICKED_OUT);
        }
    }
}
