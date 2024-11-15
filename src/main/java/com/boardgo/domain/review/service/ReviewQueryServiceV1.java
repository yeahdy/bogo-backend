package com.boardgo.domain.review.service;

import static com.boardgo.common.utils.CustomStringUtils.stringToLongList;

import com.boardgo.common.exception.CustomNoSuchElementException;
import com.boardgo.domain.mapper.ReviewMapper;
import com.boardgo.domain.review.entity.EvaluationTagEntity;
import com.boardgo.domain.review.entity.EvaluationType;
import com.boardgo.domain.review.repository.EvaluationTagRepository;
import com.boardgo.domain.review.repository.ReviewRepository;
import com.boardgo.domain.review.repository.projection.MyEvaluationTagProjection;
import com.boardgo.domain.review.repository.projection.ReviewCountProjection;
import com.boardgo.domain.review.repository.projection.ReviewMeetingReviewsProjection;
import com.boardgo.domain.review.service.response.MyEvaluationTagResponse;
import com.boardgo.domain.review.service.response.MyEvaluationTagsResponse;
import com.boardgo.domain.review.service.response.MyReviewsResponse;
import com.boardgo.domain.review.service.response.ReviewMeetingReviewsResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewQueryServiceV1 implements ReviewQueryUseCase {

    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final EvaluationTagRepository evaluationTagRepository;

    /***
     * 모임을 함께 한 참여자에게 리뷰를 작성할 경우 모임 id 가 조회된다.
     * (참여자 모두에게 리뷰를 작성하지 않아도 조회 됨)
     * @return 참여자에게 리뷰를 작성한 모임 id 목록
     */
    @Override
    public List<Long> findMeetingIdsOfWrittenReview(Long userId) {
        return reviewRepository.findFinishedReview(userId);
    }

    /***
     * 모임 별로 리뷰 작성 갯수 카운팅
     * @return Map<모임Id, 리뷰작성갯수>
     */
    @Override
    public Map<Long, Integer> countReview(Long userId) {
        List<ReviewCountProjection> reviewCountList =
                reviewRepository.countReviewByReviewerId(userId);
        return reviewCountList.stream()
                .collect(
                        Collectors.toMap(
                                ReviewCountProjection::getMeetingId,
                                ReviewCountProjection::getReviewCount));
    }

    @Override
    public List<Long> getReviewMeetingParticipants(Long meetingId, Long reviewerId) {
        return reviewRepository.findAllRevieweeId(reviewerId, meetingId);
    }

    @Override
    public List<ReviewMeetingReviewsResponse> getReviewMeetingReviews(
            Long meetingId, Long reviewerId) {
        List<ReviewMeetingReviewsProjection> meetingReviews =
                reviewRepository.findMeetingReviews(meetingId, reviewerId);
        if (meetingReviews.isEmpty()) {
            throw new CustomNoSuchElementException("작성한 모임의 리뷰");
        }

        List<ReviewMeetingReviewsResponse> reviewMeetingReviewsResponses = new ArrayList<>();
        List<List<String>> collect =
                meetingReviews.stream()
                        .map(ReviewMeetingReviewsProjection::evaluationTagIds)
                        .collect(Collectors.toList());
        int i = 0;
        for (ReviewMeetingReviewsProjection meetingReviewsProjection : meetingReviews) {
            // FIXME: 퍼사드패턴으로 리팩토링 필요
            ReviewMeetingReviewsResponse reviewMeetingReviewsResponse =
                    getMeetingReviews(collect.get(i), meetingReviewsProjection);
            reviewMeetingReviewsResponses.add(reviewMeetingReviewsResponse);
            i++;
        }
        return reviewMeetingReviewsResponses;
    }

    /***
     * 리뷰 별 평가태그 긍정적/부정적 문구 데이터 정제
     * @param strings 평가태그 고유 id 목록
     * @param meetingReviewsProjection 리뷰 정보를 담은 데이터
     */
    private ReviewMeetingReviewsResponse getMeetingReviews(
            List<String> strings, ReviewMeetingReviewsProjection meetingReviewsProjection) {
        List<Long> evaluationIds = stringToLongList(strings);
        List<EvaluationTagEntity> evaluationTagEntities =
                evaluationTagRepository.findAllById(evaluationIds);

        List<String> positiveTags = new ArrayList<>();
        List<String> negativeTags = new ArrayList<>();
        for (EvaluationTagEntity evaluationTag : evaluationTagEntities) {
            switch (evaluationTag.getEvaluationType()) {
                case POSITIVE -> positiveTags.add(evaluationTag.getTagPhrase());
                case NEGATIVE -> negativeTags.add(evaluationTag.getTagPhrase());
            }
        }
        return reviewMapper.toReviewMeetingReviewsResponse(
                meetingReviewsProjection, positiveTags, negativeTags);
    }

    @Override
    public MyReviewsResponse getMyReviews(Long userId) {
        Double averageRating = getAverageRating(userId);
        MyEvaluationTagsResponse myEvaluationTags = getMyEvaluationTags(userId);
        return new MyReviewsResponse(
                averageRating, myEvaluationTags.positiveTags(), myEvaluationTags.negativeTags());
    }

    // TODO 캐싱처리 //FIXME: 퍼사드패턴으로 리팩토링 필요
    @Override
    public MyEvaluationTagsResponse getMyEvaluationTags(Long userId) {
        List<List<String>> evaluationTags = reviewRepository.findMyEvaluationTags(userId);
        Map<Long, Integer> evaluationTagsMap = calculateEvaluationTags(evaluationTags);

        // 긍정적 태그
        List<MyEvaluationTagProjection> myPositiveEvaluationTags =
                evaluationTagRepository.findEvaluationTagsById(
                        EvaluationType.POSITIVE, evaluationTagsMap.keySet());
        List<MyEvaluationTagResponse> positiveTags =
                refineEvaluationTag(myPositiveEvaluationTags, evaluationTagsMap);
        // 부정적 태그
        List<MyEvaluationTagProjection> myNegativeEvaluationTags =
                evaluationTagRepository.findEvaluationTagsById(
                        EvaluationType.NEGATIVE, evaluationTagsMap.keySet());
        List<MyEvaluationTagResponse> negativeTags =
                refineEvaluationTag(myNegativeEvaluationTags, evaluationTagsMap);
        return new MyEvaluationTagsResponse(positiveTags, negativeTags);
    }

    /***
     * 평가 태그 중복 갯수 세기
     * @param evaluationTags 리뷰어별 평가태그 리스트
     * @return key-평가태그 id, value-중복갯수
     */
    private Map<Long, Integer> calculateEvaluationTags(List<List<String>> evaluationTags) {
        return evaluationTags.stream()
                .flatMap(List::stream)
                .map(Long::valueOf)
                .collect(Collectors.toMap(tag -> tag, tag -> 1, Integer::sum));
    }

    /***
     * 평가태그 문구와 중복갯수 데이터 정제하기
     * @param myEvaluationTags 평가태그 문구
     * @param evaluationTagsMap 평가태그 중복갯수
     */
    private List<MyEvaluationTagResponse> refineEvaluationTag(
            List<MyEvaluationTagProjection> myEvaluationTags,
            Map<Long, Integer> evaluationTagsMap) {
        List<MyEvaluationTagResponse> evaluationTags = new ArrayList<>();

        for (MyEvaluationTagProjection myTag : myEvaluationTags) {
            Integer count = evaluationTagsMap.get(myTag.evaluationTagId());
            evaluationTags.add(new MyEvaluationTagResponse(count, myTag.tagPhrase()));
        }
        return evaluationTags;
    }

    @Override
    public Double getAverageRating(Long revieweeId) {
        Double averageRating = reviewRepository.findRatingAvgByRevieweeId(revieweeId);
        return averageRating == null ? 0.0 : Math.round(averageRating * 10) / 10.0; // 소수점 한자리
    }

    @Override
    public boolean existReview(Long reviewerId, Long meetingId, Long revieweeId) {
        return reviewRepository.existsByReviewerIdAndMeetingIdAndRevieweeId(
                reviewerId, meetingId, revieweeId);
    }
}
