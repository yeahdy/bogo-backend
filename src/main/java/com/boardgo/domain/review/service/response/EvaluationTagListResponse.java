package com.boardgo.domain.review.service.response;

import java.util.List;

public record EvaluationTagListResponse(
        List<EvaluationTagResponse> positiveTags, List<EvaluationTagResponse> negativeTags) {}
