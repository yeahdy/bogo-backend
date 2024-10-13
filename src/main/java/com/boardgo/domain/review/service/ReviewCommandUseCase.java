package com.boardgo.domain.review.service;

import com.boardgo.domain.review.controller.request.ReviewCreateRequest;

public interface ReviewCommandUseCase {
    void create(ReviewCreateRequest reviewType, Long reviewerId);
}
