package com.boardgo.domain.review.service.facade;

import com.boardgo.domain.review.controller.request.ReviewCreateRequest;

public interface ReviewCommandFacade {
    void create(ReviewCreateRequest reviewType, Long reviewerId);
}
