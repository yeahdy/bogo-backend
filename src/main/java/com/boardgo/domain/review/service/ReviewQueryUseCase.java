package com.boardgo.domain.review.service;

import com.boardgo.domain.review.service.response.MyEvaluationTagsResponse;
import com.boardgo.domain.review.service.response.MyReviewsResponse;
import com.boardgo.domain.review.service.response.ReviewMeetingReviewsResponse;
import java.util.List;
import java.util.Map;

public interface ReviewQueryUseCase {

    List<Long> findMeetingIdsOfWrittenReview(Long userId);

    Map<Long, Integer> countReview(Long userId);

    List<Long> getReviewMeetingParticipants(Long meetingId, Long reviewerId);

    List<ReviewMeetingReviewsResponse> getReviewMeetingReviews(Long meetingId, Long reviewerId);

    MyReviewsResponse getMyReviews(Long userId);

    Double getAverageRating(Long revieweeId);

    MyEvaluationTagsResponse getMyEvaluationTags(Long userId);

    boolean existReview(Long reviewerId, Long meetingId, Long revieweeId);
}
