package com.boardgo.domain.review.repository.projection;

import com.querydsl.core.annotations.QueryProjection;
import java.util.List;

public record ReviewMeetingReviewsProjection(
        Long reviewId, String revieweeName, int rating, List<String> evaluationTagIds) {
    @QueryProjection
    public ReviewMeetingReviewsProjection {}
}
