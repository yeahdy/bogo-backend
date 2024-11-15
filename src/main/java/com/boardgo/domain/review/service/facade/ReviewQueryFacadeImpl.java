package com.boardgo.domain.review.service.facade;

import static com.boardgo.domain.meeting.entity.enums.ParticipantType.LEADER;
import static com.boardgo.domain.meeting.entity.enums.ParticipantType.PARTICIPANT;

import com.boardgo.domain.mapper.ReviewMapper;
import com.boardgo.domain.meeting.entity.MeetingEntity;
import com.boardgo.domain.meeting.service.MeetingParticipantQueryUseCase;
import com.boardgo.domain.meeting.service.MeetingQueryUseCase;
import com.boardgo.domain.meeting.service.response.ParticipationCountResponse;
import com.boardgo.domain.review.entity.enums.ReviewType;
import com.boardgo.domain.review.service.ReviewQueryUseCase;
import com.boardgo.domain.review.service.response.ReviewMeetingParticipantsResponse;
import com.boardgo.domain.review.service.response.ReviewMeetingResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewQueryFacadeImpl implements ReviewQueryFacade {
    private final ReviewQueryUseCase reviewQueryUseCase;
    private final MeetingParticipantQueryUseCase meetingParticipantQueryUseCase;
    private final MeetingQueryUseCase meetingQueryUseCase;
    private final ReviewMapper reviewMapper;

    @Override
    public List<ReviewMeetingParticipantsResponse> getMeetingParticipantsToReview(
            Long meetingId, Long reviewerId) {
        List<Long> revieweeIds =
                reviewQueryUseCase.getReviewMeetingParticipants(meetingId, reviewerId);
        revieweeIds.add(reviewerId); // 본인 리뷰 작성자 목록 표출 제외
        return meetingParticipantQueryUseCase.findMeetingParticipantsToReview(
                revieweeIds, meetingId);
    }

    @Override
    public List<ReviewMeetingResponse> getMeetingsToReview(ReviewType reviewType, Long userId) {
        switch (reviewType) {
            case PRE_PROGRESS -> {
                List<Long> finishedReviewMeetingIds = findFinishedReviewMeetingIds(userId);
                // 모임 중 참여자가 본인 1명 뿐인 경우 리뷰 작성 모임 항목에서 제외
                Map<Long, Long> meetingParticipantsCount =
                        meetingParticipantQueryUseCase.countMeetingParticipants(userId, 1L);
                finishedReviewMeetingIds.addAll(meetingParticipantsCount.keySet());
                return meetingQueryUseCase.findReviewableMeeting(userId, finishedReviewMeetingIds);
            }
            case FINISH -> {
                List<Long> meetingIds = reviewQueryUseCase.findMeetingIdsOfWrittenReview(userId);
                List<MeetingEntity> meetingEntityList = meetingQueryUseCase.findAllById(meetingIds);
                return reviewMapper.toReviewMeetingResponses(meetingEntityList);
            }
        }
        return null;
    }

    /***
     * 모임 별로 참여자 모두에게 리뷰를 전부 작성한 모임 찾기
     * (리뷰 작성이 필요없는 모임)
     * @return 리뷰 작성 완료 모임 ID 목록
     */
    public List<Long> findFinishedReviewMeetingIds(Long userId) {
        Map<Long, Integer> reviewCountMap = reviewQueryUseCase.countReview(userId);

        List<ParticipationCountResponse> participationCountList =
                meetingParticipantQueryUseCase.countMeetingParticipants(
                        reviewCountMap.keySet(), List.of(LEADER, PARTICIPANT));

        List<Long> reviewFinished = new ArrayList<>();
        for (ParticipationCountResponse participationCount : participationCountList) {
            Long meetingId = participationCount.metingId();
            Integer reviewCount = reviewCountMap.get(meetingId);
            Integer participantCount = participationCount.participationCount() - 1; // 본인 제외
            if (reviewCount.equals(participantCount)) {
                reviewFinished.add(meetingId);
            }
        }
        return reviewFinished;
    }
}
