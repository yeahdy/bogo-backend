package com.boardgo.domain.review.service.response;

import java.util.List;

public record MyReviewsResponse(
        Double averageRating,
        List<MyEvaluationTagResponse> positiveTags,
        List<MyEvaluationTagResponse> negativeTags) {}
