package com.boardgo.domain.meeting.repository;

import java.util.List;

import com.boardgo.domain.meeting.repository.projection.ReviewMeetingParticipantsProjection;

public interface MeetingParticipantDslRepository {
    List<ReviewMeetingParticipantsProjection> findReviewMeetingParticipants(
            List<Long> revieweeIds, Long meetingId);

    List<Long> getMeetingIdByNotEqualsOut(Long userId);
}
