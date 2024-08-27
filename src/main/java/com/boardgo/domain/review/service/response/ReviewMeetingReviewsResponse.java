package com.boardgo.domain.review.service.response;

import java.util.List;

public record ReviewMeetingReviewsResponse(
        Long reviewId,
        String revieweeName,
        int rating,
        List<String> positiveTags,
        List<String> negativeTags) {}
