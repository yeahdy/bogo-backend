package com.boardgo.domain.meeting.service;

import com.boardgo.domain.meeting.service.response.ParticipantOutResponse;

public interface MeetingParticipantQueryUseCase {

    ParticipantOutResponse getOutState(Long meetingId);

    int getMeetingCount(Long userId);
}
