package com.boardgo.domain.review.service;

import com.boardgo.domain.review.entity.enums.ReviewType;
import com.boardgo.domain.review.service.response.MyEvaluationTagsResponse;
import com.boardgo.domain.review.service.response.MyReviewsResponse;
import com.boardgo.domain.review.service.response.ReviewMeetingResponse;
import com.boardgo.domain.review.service.response.ReviewMeetingReviewsResponse;
import java.util.List;

public interface ReviewQueryUseCase {

    List<ReviewMeetingResponse> getReviewMeetings(ReviewType reviewType, Long userId);

    List<Long> getReviewMeetingParticipants(Long meetingId, Long reviewerId);

    List<ReviewMeetingReviewsResponse> getReviewMeetingReviews(Long meetingId, Long reviewerId);

    MyReviewsResponse getMyReviews(Long userId);

    Double getAverageRating(Long revieweeId);

    MyEvaluationTagsResponse getMyEvaluationTags(Long userId);

    boolean existReview(Long reviewerId, Long meetingId, Long revieweeId);
}
