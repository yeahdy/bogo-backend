package com.boardgo.domain.review.service.response;

import com.boardgo.domain.review.entity.EvaluationType;

public record EvaluationTagResponse(
        Long evaluationTagId, String tagPhrase, EvaluationType evaluationType) {}
