package com.boardgo.domain.review.service.facade;

import com.boardgo.domain.review.service.response.ReviewMeetingParticipantsResponse;
import java.util.List;

public interface ReviewQueryFacade {

    List<ReviewMeetingParticipantsResponse> getMeetingParticipantsToReview(
            Long meetingId, Long reviewerId);
}
