package com.boardgo.domain.review.service.facade;

import com.boardgo.domain.review.entity.enums.ReviewType;
import com.boardgo.domain.review.service.response.ReviewMeetingParticipantsResponse;
import com.boardgo.domain.review.service.response.ReviewMeetingResponse;
import java.util.List;

public interface ReviewQueryFacade {

    List<ReviewMeetingParticipantsResponse> getMeetingParticipantsToReview(
            Long meetingId, Long reviewerId);

    List<ReviewMeetingResponse> getMeetingsToReview(ReviewType reviewType, Long userId);
}
