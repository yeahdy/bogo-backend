package com.boardgo.domain.review.service.response;

import java.util.List;

public record MyEvaluationTagsResponse(
        List<MyEvaluationTagResponse> positiveTags, List<MyEvaluationTagResponse> negativeTags) {}
