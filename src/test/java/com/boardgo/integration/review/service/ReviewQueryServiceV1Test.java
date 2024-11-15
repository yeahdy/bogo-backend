package com.boardgo.integration.review.service;

import static com.boardgo.integration.fixture.EvaluationTagFixture.getEvaluationTagEntity;
import static com.boardgo.integration.fixture.ReviewFixture.getReview;
import static org.assertj.core.api.Assertions.assertThat;

import com.boardgo.domain.review.entity.ReviewEntity;
import com.boardgo.domain.review.repository.EvaluationTagRepository;
import com.boardgo.domain.review.repository.ReviewRepository;
import com.boardgo.domain.review.service.ReviewQueryUseCase;
import com.boardgo.domain.review.service.response.MyEvaluationTagResponse;
import com.boardgo.domain.review.service.response.MyReviewsResponse;
import com.boardgo.integration.support.IntegrationTestSupport;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ReviewQueryServiceV1Test extends IntegrationTestSupport {

    @Autowired private ReviewQueryUseCase reviewQueryUseCase;
    @Autowired private ReviewRepository reviewRepository;
    @Autowired private EvaluationTagRepository evaluationTagRepository;

    @Test
    @DisplayName("내 평가태그 조회 시 모든 모임에서 받은 각 평가태그의 갯수를 합산해서 보여준다")
    void 내_평가태그_조회_시_모든_모임에서_받은_각_평가태그의_갯수를_합산해서_보여준다() {
        // given
        Long userId = 1L;
        // 평가태그
        evaluationTagRepository.saveAll(getEvaluationTagEntity());
        // 리뷰
        List<ReviewEntity> reviewEntities = new ArrayList<>();
        int count = 10;
        for (long i = 0; i < count; i++) {
            reviewEntities.add(reviewRepository.save(getReview(i + 2, userId, i + 1)));
            // evaluationTags: "1", "2", "3", "6", "7", "10", "12"
        }

        // when
        MyReviewsResponse myReview = reviewQueryUseCase.getMyReviews(userId);

        // then
        for (MyEvaluationTagResponse myEvaluationTag : myReview.positiveTags()) {
            assertThat(myEvaluationTag.count()).isEqualTo(count);
        }
        for (MyEvaluationTagResponse myEvaluationTag : myReview.negativeTags()) {
            assertThat(myEvaluationTag.count()).isEqualTo(count);
        }
    }

    @Test
    @DisplayName("리뷰 평균 점수는 소수점 한자리 까지 이다")
    void 리뷰_평균_점수는_소수점_한자리_까지_이다() {
        // given
        Long revieweeId = 1L;
        for (long i = 0; i < 10; i++) {
            reviewRepository.save(getReview(i + 1, revieweeId, i + 1));
        }

        // when
        Double averageRating = reviewQueryUseCase.getAverageRating(revieweeId);

        // then
        String averageRatingString = averageRating.toString();
        assertThat(averageRatingString).isNotEmpty();
        assertThat(averageRatingString.length()).isEqualTo(3); // 소수점 1자리는 0.0 == 문자열 길이 3자리
    }

    @Test
    @DisplayName("참여한 모임에서 본인이 작성한 전체 리뷰를 조회한다")
    void 참여한_모임에서_본인이_작성한_전체_리뷰를_조회한다() {
        // given
        Long meetingId = 1L;
        Long reviewerId = 1L;
        reviewRepository.save(getReview(reviewerId, 2L, meetingId));
        reviewRepository.save(getReview(reviewerId, 3L, meetingId));
        reviewRepository.save(getReview(reviewerId, 4L, meetingId));
        reviewRepository.save(getReview(reviewerId, 5L, meetingId));

        // when
        List<Long> reviewIds =
                reviewQueryUseCase.getReviewMeetingParticipants(meetingId, reviewerId);

        // then
        assertThat(reviewIds).isNotEmpty();
        assertThat(reviewIds.containsAll(List.of(2L, 3L, 4L, 5L))).isTrue();
    }
}
