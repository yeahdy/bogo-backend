package com.boardgo.domain.meeting.service;

public interface MeetingParticipantWaitingCommandUseCase {
    void deleteByMeetingId(Long meetingId);
}
