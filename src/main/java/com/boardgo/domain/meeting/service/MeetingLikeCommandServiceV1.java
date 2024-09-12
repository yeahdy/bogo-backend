package com.boardgo.domain.meeting.service;

import com.boardgo.common.exception.CustomIllegalArgumentException;
import com.boardgo.common.exception.CustomNoSuchElementException;
import com.boardgo.common.utils.SecurityUtils;
import com.boardgo.domain.meeting.entity.MeetingEntity;
import com.boardgo.domain.meeting.repository.MeetingLikeRepository;
import com.boardgo.domain.meeting.repository.MeetingRepository;
import com.boardgo.domain.user.repository.UserRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MeetingLikeCommandServiceV1 implements MeetingLikeCommandUseCase {

    private final MeetingLikeRepository meetingLikeRepository;
    private final MeetingRepository meetingRepository;
    private final UserRepository userRepository;

    @Override
    public void createMany(List<Long> meetingIdList) {
        checkCreateLikeValidation(meetingIdList);
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

    private void checkCreateLikeValidation(List<Long> meetingIdList) {
        checkUserExist();
        checkMeetingIdListExist(meetingIdList);
    }

    private void checkMeetingIdListExist(List<Long> meetingIdList) {
        List<MeetingEntity> meetingEntities = meetingRepository.findByIdIn(meetingIdList);
        Set<Long> meetingIdSet = new HashSet<>(meetingIdList);
        if (meetingEntities.size() != meetingIdList.size()) {
            throw new CustomIllegalArgumentException("모임 ID 중 없는 모임이 존재합니다.");
        }

        if (meetingEntities.size() != meetingIdSet.size()) {
            throw new CustomIllegalArgumentException("모임 ID 중 중복된 모임이 있습니다.");
        }
    }

    private void checkUserExist() {
        userRepository
                .findById(SecurityUtils.currentUserId())
                .orElseThrow(() -> new CustomNoSuchElementException("유저"));
    }
}
