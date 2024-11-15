package com.boardgo.domain.meeting.repository;

import com.boardgo.domain.meeting.repository.projection.MeetingParticipantsCountProjection;
import com.boardgo.domain.meeting.repository.projection.ReviewMeetingParticipantsProjection;
import com.boardgo.domain.user.repository.projection.UserParticipantProjection;
import java.util.List;

public interface MeetingParticipantDslRepository {
    List<ReviewMeetingParticipantsProjection> findMeetingParticipantsToReview(
            List<Long> revieweeIds, Long meetingId);

    List<UserParticipantProjection> findParticipantListByMeetingId(Long meetingId);

    List<Long> getMeetingIdByNotEqualsOut(Long userId);

    List<MeetingParticipantsCountProjection> countMeetingParticipantsByUserInfoId(
            Long userId, Long participantCount);
}
