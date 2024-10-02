package com.boardgo.domain.meeting.service;

import java.util.List;

import com.boardgo.domain.meeting.service.response.ParticipantOutResponse;

public interface MeetingParticipantQueryUseCase {

    ParticipantOutResponse getOutState(Long meetingId);

    int getMeetingCount(Long userId);

    List<Long> getMeetingIdByNotEqualsOut(Long userId);
}
