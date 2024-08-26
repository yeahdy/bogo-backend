package com.boardgo.domain.review.service;

import static com.boardgo.domain.meeting.entity.enums.ParticipantType.LEADER;
import static com.boardgo.domain.meeting.entity.enums.ParticipantType.PARTICIPANT;

import com.boardgo.common.exception.CustomIllegalArgumentException;
import com.boardgo.common.exception.CustomNullPointException;
import com.boardgo.common.exception.DuplicateException;
import com.boardgo.domain.mapper.ReviewMapper;
import com.boardgo.domain.meeting.entity.MeetingEntity;
import com.boardgo.domain.meeting.repository.MeetingParticipantRepository;
import com.boardgo.domain.meeting.repository.MeetingRepository;
import com.boardgo.domain.meeting.repository.projection.MeetingReviewProjection;
import com.boardgo.domain.meeting.repository.projection.ParticipationCountProjection;
import com.boardgo.domain.review.controller.request.ReviewCreateRequest;
import com.boardgo.domain.review.entity.ReviewEntity;
import com.boardgo.domain.review.entity.enums.ReviewType;
import com.boardgo.domain.review.repository.ReviewRepository;
import com.boardgo.domain.review.repository.projection.ReviewCountProjection;
import com.boardgo.domain.review.service.response.ReviewMeetingResponse;
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
            if (reviewCount == participantCount) {
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
}
