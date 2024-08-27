package com.boardgo.domain.review.repository;

import com.boardgo.domain.review.repository.projection.ReviewMeetingReviewsProjection;
import java.util.List;

public interface ReviewDslRepository {

    List<ReviewMeetingReviewsProjection> findMeetingReviews(Long meetingId, Long reviewerId);
}
