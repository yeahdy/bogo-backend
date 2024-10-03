package com.boardgo.domain.meeting.service;

import com.boardgo.domain.meeting.service.response.ParticipantOutResponse;
import com.boardgo.domain.meeting.service.response.UserParticipantResponse;
import java.util.List;

public interface MeetingParticipantQueryUseCase {

    ParticipantOutResponse getOutState(Long meetingId);

    int getMeetingCount(Long userId);

    List<UserParticipantResponse> findByMeetingId(Long meetingId);

    List<Long> getMeetingIdByNotEqualsOut(Long userId);
}
