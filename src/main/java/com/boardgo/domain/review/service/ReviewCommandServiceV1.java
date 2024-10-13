package com.boardgo.domain.review.service;

import com.boardgo.domain.mapper.ReviewMapper;
import com.boardgo.domain.review.controller.request.ReviewCreateRequest;
import com.boardgo.domain.review.entity.ReviewEntity;
import com.boardgo.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewCommandServiceV1 implements ReviewCommandUseCase {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;

    @Override
    public void create(ReviewCreateRequest createRequest, Long reviewerId) {
        ReviewEntity reviewEntity =
                reviewMapper.toReviewEntity(
                        createRequest, createRequest.evaluationTagList(), reviewerId);
        reviewRepository.save(reviewEntity);
    }
}
