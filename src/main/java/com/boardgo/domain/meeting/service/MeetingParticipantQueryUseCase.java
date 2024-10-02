package com.boardgo.domain.meeting.service;

import java.util.List;

import com.boardgo.domain.meeting.service.response.ParticipantOutResponse;
import com.boardgo.domain.meeting.service.response.UserParticipantResponse;

public interface MeetingParticipantQueryUseCase {

    ParticipantOutResponse getOutState(Long meetingId);

    int getMeetingCount(Long userId);

    List<UserParticipantResponse> findByMeetingId(Long meetingId);
  
    List<Long> getMeetingIdByNotEqualsOut(Long userId);
}
