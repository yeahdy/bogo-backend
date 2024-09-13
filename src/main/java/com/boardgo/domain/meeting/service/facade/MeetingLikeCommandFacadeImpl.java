package com.boardgo.domain.meeting.service.facade;

import com.boardgo.common.exception.CustomIllegalArgumentException;
import com.boardgo.common.exception.CustomNoSuchElementException;
import com.boardgo.domain.meeting.entity.MeetingEntity;
import com.boardgo.domain.meeting.service.MeetingLikeCommandUseCase;
import com.boardgo.domain.meeting.service.MeetingQueryUseCase;
import com.boardgo.domain.user.service.UserQueryUseCase;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MeetingLikeCommandFacadeImpl implements MeetingLikeCommandFacade {

    private final MeetingLikeCommandUseCase meetingLikeCommandUseCase;
    private final UserQueryUseCase userQueryUseCase;
    private final MeetingQueryUseCase meetingQueryUseCase;

    @Override
    public void createMany(List<Long> meetingIdList, Long userId) {
        checkCreateLikeValidation(meetingIdList, userId);
        meetingLikeCommandUseCase.createMany(meetingIdList);
    }

    private void checkCreateLikeValidation(List<Long> meetingIdList, Long userId) {
        checkUserExist(userId);
        checkMeetingIdListExist(meetingIdList);
    }

    private void checkMeetingIdListExist(List<Long> meetingIdList) {
        Set<Long> meetingIdSet = new HashSet<>(meetingIdList);
        List<MeetingEntity> meetingEntities =
                meetingQueryUseCase.findByIdIn(meetingIdSet.stream().toList());
        if (meetingEntities.size() != meetingIdSet.size()) {
            throw new CustomIllegalArgumentException("모임 ID 중 없는 모임이 존재합니다.");
        }
    }

    private void checkUserExist(Long userId) {
        if (!userQueryUseCase.existById(userId)) {
            throw new CustomNoSuchElementException("유저");
        }
    }
}
