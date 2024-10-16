package com.boardgo.domain.meeting.service.facade;

public interface MeetingParticipantCommandFacade {
    void outMeeting(Long meetingId, Long userId, boolean isKicked);
}
