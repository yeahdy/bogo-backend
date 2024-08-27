package com.boardgo.integration.fixture;

import com.boardgo.domain.review.entity.ReviewEntity;
import java.util.List;

public abstract class ReviewFixture {

    public static ReviewEntity getReview(Long reviewerId, Long revieweeId, Long meetingId) {
        return ReviewEntity.builder()
                .reviewerId(reviewerId)
                .revieweeId(revieweeId)
                .meetingId(meetingId)
                .rating(4)
                .evaluationTags(List.of("1", "2", "3", "6", "7", "10", "12"))
                .build();
    }
}
