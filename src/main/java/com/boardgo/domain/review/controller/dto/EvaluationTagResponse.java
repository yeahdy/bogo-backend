package com.boardgo.domain.review.controller.dto;

import com.boardgo.domain.review.entity.EvaluationType;

public record EvaluationTagResponse(
        Long evaluationTagId, String tagPhrase, EvaluationType evaluationType) {}
