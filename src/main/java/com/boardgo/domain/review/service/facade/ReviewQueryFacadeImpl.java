package com.boardgo.domain.review.service.facade;

import com.boardgo.domain.meeting.service.MeetingParticipantQueryUseCase;
import com.boardgo.domain.review.service.ReviewQueryUseCase;
import com.boardgo.domain.review.service.response.ReviewMeetingParticipantsResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewQueryFacadeImpl implements ReviewQueryFacade {
    private final ReviewQueryUseCase reviewQueryUseCase;
    private final MeetingParticipantQueryUseCase meetingParticipantQueryUseCase;

    @Override
    public List<ReviewMeetingParticipantsResponse> getMeetingParticipantsToReview(
            Long meetingId, Long reviewerId) {
        List<Long> revieweeIds =
                reviewQueryUseCase.getReviewMeetingParticipants(meetingId, reviewerId);
        revieweeIds.add(reviewerId); // 본인 리뷰 작성자 목록 표출 제외
        return meetingParticipantQueryUseCase.findMeetingParticipantsToReview(
                revieweeIds, meetingId);
    }
}
