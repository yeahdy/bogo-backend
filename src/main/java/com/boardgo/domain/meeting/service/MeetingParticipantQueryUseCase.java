package com.boardgo.domain.meeting.service;

import com.boardgo.domain.meeting.service.response.ParticipantOutResponse;
import com.boardgo.domain.meeting.service.response.UserParticipantResponse;
import com.boardgo.domain.review.service.response.ReviewMeetingParticipantsResponse;
import java.util.List;

public interface MeetingParticipantQueryUseCase {

    ParticipantOutResponse getOutState(Long meetingId);

    int getMeetingCount(Long userId);

    List<UserParticipantResponse> findByMeetingId(Long meetingId);

    List<Long> getMeetingIdByNotEqualsOut(Long userId);

    void checkMeetingTogether(Long meetingId, List<Long> userIds);

    List<ReviewMeetingParticipantsResponse> findMeetingParticipantsToReview(
            List<Long> revieweeIds, Long meetingId);
}
