package com.boardgo.domain.review.service;

import static com.boardgo.common.utils.CustomStringUtils.*;
import static com.boardgo.domain.meeting.entity.enums.ParticipantType.*;

import com.boardgo.common.exception.CustomIllegalArgumentException;
import com.boardgo.common.exception.CustomNoSuchElementException;
import com.boardgo.common.exception.CustomNullPointException;
import com.boardgo.common.exception.DuplicateException;
import com.boardgo.domain.mapper.ReviewMapper;
import com.boardgo.domain.meeting.entity.MeetingEntity;
import com.boardgo.domain.meeting.repository.MeetingParticipantRepository;
import com.boardgo.domain.meeting.repository.MeetingRepository;
import com.boardgo.domain.meeting.repository.projection.MeetingReviewProjection;
import com.boardgo.domain.meeting.repository.projection.ParticipationCountProjection;
import com.boardgo.domain.meeting.repository.projection.ReviewMeetingParticipantsProjection;
import com.boardgo.domain.review.controller.request.ReviewCreateRequest;
import com.boardgo.domain.review.entity.EvaluationTagEntity;
import com.boardgo.domain.review.entity.EvaluationType;
import com.boardgo.domain.review.entity.ReviewEntity;
import com.boardgo.domain.review.entity.enums.ReviewType;
import com.boardgo.domain.review.repository.EvaluationTagRepository;
import com.boardgo.domain.review.repository.ReviewRepository;
import com.boardgo.domain.review.repository.projection.MyEvaluationTagProjection;
import com.boardgo.domain.review.repository.projection.ReviewCountProjection;
import com.boardgo.domain.review.repository.projection.ReviewMeetingReviewsProjection;
import com.boardgo.domain.review.service.response.MyEvaluationTagResponse;
import com.boardgo.domain.review.service.response.MyReviewsResponse;
import com.boardgo.domain.review.service.response.ReviewMeetingParticipantsResponse;
import com.boardgo.domain.review.service.response.ReviewMeetingResponse;
import com.boardgo.domain.review.service.response.ReviewMeetingReviewsResponse;
import com.boardgo.domain.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewQueryServiceV1 implements ReviewUseCase {

    private final ReviewRepository reviewRepository;
    private final MeetingRepository meetingRepository;
    private final MeetingParticipantRepository meetingParticipantRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;
    private final EvaluationTagRepository evaluationTagRepository;

    @Override
    public List<ReviewMeetingResponse> getReviewMeetings(ReviewType reviewType, Long userId) {
        switch (reviewType) {
            case PRE_PROGRESS -> {
                List<Long> reviewFinishedMeetings = findReviewFinishedList(userId);
                List<MeetingReviewProjection> meetingPreProgressReview =
                        meetingRepository.findMeetingPreProgressReview(
                                userId, reviewFinishedMeetings);
                return reviewMapper.toReviewMeetingReviewMeetingFromProjection(
                        meetingPreProgressReview);
            }
            case FINISH -> {
                List<Long> meetingIds = reviewRepository.findFinishedReview(userId);
                List<MeetingEntity> meetingEntityList = meetingRepository.findAllById(meetingIds);
                return reviewMapper.toReviewMeetingReviewMeetingFromEntity(meetingEntityList);
            }
        }

        return List.of();
    }

    /***
     * 참여한 모임애서 참여자 모두에게 리뷰를 작성한 모임 찾기
     * @param userId
     * @return 리뷰 작성 완료 모임 ID 목록
     */
    private List<Long> findReviewFinishedList(Long userId) {
        List<ReviewCountProjection> reviewCountList =
                reviewRepository.countReviewByReviewerId(userId);

        Map<Long, Integer> reviewCountMap =
                reviewCountList.stream()
                        .collect(
                                Collectors.toMap(
                                        ReviewCountProjection::getMeetingId,
                                        ReviewCountProjection::getReviewCount));

        List<ParticipationCountProjection> participationCountList =
                meetingParticipantRepository.countMeetingParticipation(
                        reviewCountMap.keySet(), List.of(LEADER, PARTICIPANT));

        List<Long> reviewFinished = new ArrayList<>();
        for (ParticipationCountProjection participationCount : participationCountList) {
            Long meetingId = participationCount.getMeetingId();
            Integer reviewCount = reviewCountMap.get(meetingId);
            Integer participantCount = participationCount.getParticipationCount() - 1; // 본인 제외
            if (reviewCount.equals(participantCount)) {
                reviewFinished.add(meetingId);
            }
        }
        return reviewFinished;
    }

    @Override
    public void create(ReviewCreateRequest createRequest, Long reviewerId) {
        validateCreateReview(createRequest.meetingId(), createRequest.revieweeId(), reviewerId);
        ReviewEntity reviewEntity =
                reviewMapper.toReviewEntity(
                        createRequest, createRequest.evaluationTagList(), reviewerId);
        reviewRepository.save(reviewEntity);
    }

    /***
     * 모임 참가하기 유효성 검증
     * @param meetingId 모임 고유 id
     * @param revieweeId 리뷰 받는 참여자 id
     * @param reviewerId 리뷰 작성자 id
     */

    private void validateCreateReview(Long meetingId, Long revieweeId, Long reviewerId) {
        MeetingEntity meetingEntity =
                meetingRepository
                        .findById(meetingId)
                        .orElseThrow(() -> new CustomNullPointException("모임이 존재하지 않습니다"));
        if (!meetingEntity.isFinishState()) {
            throw new CustomIllegalArgumentException("종료된 모임이 아닙니다");
        }
        if (!userRepository.existsById(revieweeId)) {
            throw new CustomNullPointException("회원이 존재하지 않습니다");
        }

        boolean written =
                reviewRepository.existsByReviewerIdAndMeetingIdAndRevieweeId(
                        reviewerId, meetingId, revieweeId);
        if (written) {
            throw new DuplicateException("이미 작성된 리뷰 입니다");
        }

        Long meetingParticipantCount =
                meetingParticipantRepository.countMeetingParticipant(
                        meetingId, List.of(revieweeId, reviewerId));
        final int TOGETHER = 2;
        if (meetingParticipantCount != TOGETHER) {
            throw new CustomIllegalArgumentException("모임을 함께 참여하지 않았습니다");
        }
    }

    @Override
    public List<ReviewMeetingParticipantsResponse> getReviewMeetingParticipants(
            Long meetingId, Long reviewerId) {
        List<Long> revieweeIds = reviewRepository.findAllRevieweeId(reviewerId, meetingId);
        revieweeIds.add(reviewerId); // 본인 리뷰 작성자 목록 표출 제외

        List<ReviewMeetingParticipantsProjection> reviewMeetingParticipants =
                meetingParticipantRepository.findReviewMeetingParticipants(revieweeIds, meetingId);
        if (reviewMeetingParticipants.isEmpty()) {
            throw new CustomNoSuchElementException("리뷰를 작성할 참여자");
        }

        List<ReviewMeetingParticipantsResponse> reviewMeetingParticipantsResponseList =
                reviewMapper.toReviewMeetingParticipantsList(reviewMeetingParticipants);
        return reviewMeetingParticipantsResponseList;
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
        Double averageRating = reviewRepository.findRatingAvgByRevieweeId(userId);
        List<List<String>> evaluationTags = reviewRepository.findMyEvaluationTags(userId);
        // FIXME 스케줄러를 통해 카운팅 누적 카운팅 계산하도록 기능 개선 필요
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
        return new MyReviewsResponse(averageRating, positiveTags, negativeTags);
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
}
