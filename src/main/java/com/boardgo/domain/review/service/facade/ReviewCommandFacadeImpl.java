package com.boardgo.domain.review.service.facade;

import com.boardgo.common.exception.CustomIllegalArgumentException;
import com.boardgo.common.exception.CustomNullPointException;
import com.boardgo.common.exception.DuplicateException;
import com.boardgo.domain.meeting.entity.MeetingEntity;
import com.boardgo.domain.meeting.service.MeetingParticipantQueryUseCase;
import com.boardgo.domain.meeting.service.MeetingQueryUseCase;
import com.boardgo.domain.review.controller.request.ReviewCreateRequest;
import com.boardgo.domain.review.service.ReviewCommandUseCase;
import com.boardgo.domain.review.service.ReviewQueryUseCase;
import com.boardgo.domain.user.service.UserQueryUseCase;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewCommandFacadeImpl implements ReviewCommandFacade {

    private final ReviewCommandUseCase reviewCommandUseCase;
    private final ReviewQueryUseCase reviewQueryUseCase;
    private final MeetingQueryUseCase meetingQueryUseCase;
    private final UserQueryUseCase userQueryUseCase;
    private final MeetingParticipantQueryUseCase meetingParticipantQueryUseCase;

    @Override
    public void create(ReviewCreateRequest createRequest, Long reviewerId) {
        validateCreateReview(createRequest.meetingId(), createRequest.revieweeId(), reviewerId);
        reviewCommandUseCase.create(createRequest, reviewerId);
    }

    /***
     * 모임 참가하기 유효성 검증
     * @param meetingId 모임 고유 id
     * @param revieweeId 리뷰 받는 참여자 id
     * @param reviewerId 리뷰 작성자 id
     */

    private void validateCreateReview(Long meetingId, Long revieweeId, Long reviewerId) {
        MeetingEntity meetingEntity = meetingQueryUseCase.getMeeting(meetingId);
        if (!meetingEntity.isFinishState()) {
            throw new CustomIllegalArgumentException("종료된 모임이 아닙니다");
        }
        if (!userQueryUseCase.existById(revieweeId)) {
            throw new CustomNullPointException("회원이 존재하지 않습니다");
        }
        if (reviewQueryUseCase.existReview(reviewerId, meetingId, revieweeId)) {
            throw new DuplicateException("이미 작성된 리뷰 입니다");
        }
        meetingParticipantQueryUseCase.checkMeetingTogether(
                meetingId, List.of(revieweeId, reviewerId));
    }
}
