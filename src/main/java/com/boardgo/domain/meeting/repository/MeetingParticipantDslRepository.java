package com.boardgo.domain.meeting.repository;

import com.boardgo.domain.meeting.repository.projection.ReviewMeetingParticipantsProjection;
import java.util.List;

public interface MeetingParticipantDslRepository {
    List<ReviewMeetingParticipantsProjection> findReviewMeetingParticipants(
            List<Long> revieweeIds, Long meetingId);
}
