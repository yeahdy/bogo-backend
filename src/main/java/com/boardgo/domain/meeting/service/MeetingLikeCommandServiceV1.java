package com.boardgo.domain.meeting.service;

import com.boardgo.common.exception.CustomNoSuchElementException;
import com.boardgo.common.utils.SecurityUtils;
import com.boardgo.domain.meeting.repository.MeetingLikeRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MeetingLikeCommandServiceV1 implements MeetingLikeCommandUseCase {

    private final MeetingLikeRepository meetingLikeRepository;

    @Override
    public void createMany(List<Long> meetingIdList) {
        meetingLikeRepository.bulkInsert(meetingIdList, SecurityUtils.currentUserId());
    }

    @Override
    public void deleteByUserIdAndMeetingId(Long userId, Long meetingId) {
        checkDeleteLikeValidation(meetingId, userId);
        meetingLikeRepository.deleteByUserIdAndMeetingId(userId, meetingId);
    }

    @Override
    public void deleteByMeetingId(Long meetingId) {
        meetingLikeRepository.deleteAllInBatchByMeetingId(meetingId);
    }

    private void checkDeleteLikeValidation(Long meetingId, Long userId) {
        meetingLikeRepository
                .findByUserIdAndMeetingId(userId, meetingId)
                .orElseThrow(() -> new CustomNoSuchElementException("찜"));
    }
}
