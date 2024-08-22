package com.boardgo.domain.review.controller.dto;

import java.util.List;

public record EvaluationTagListResponse(
        List<EvaluationTagResponse> positiveTags, List<EvaluationTagResponse> negativeTags) {}
