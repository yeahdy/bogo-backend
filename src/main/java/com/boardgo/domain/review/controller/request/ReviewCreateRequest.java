package com.boardgo.domain.review.controller.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

public record ReviewCreateRequest(
        @NotNull Long revieweeId,
        @NotNull Long meetingId,
        @Positive @Max(value = 5) int rating,
        @NotNull(message = "evaluationTagList") List<Long> evaluationTagList) {}
