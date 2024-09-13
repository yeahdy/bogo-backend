package com.boardgo.domain.meeting.service.facade;

import com.boardgo.common.exception.CustomNullPointException;
import com.boardgo.domain.mapper.MeetingMapper;
import com.boardgo.domain.meeting.entity.MeetingLikeEntity;
import com.boardgo.domain.meeting.entity.enums.MyPageMeetingFilter;
import com.boardgo.domain.meeting.service.MeetingLikeQueryUseCase;
import com.boardgo.domain.meeting.service.MeetingParticipantSubQueryUseCase;
import com.boardgo.domain.meeting.service.MeetingQueryUseCase;
import com.boardgo.domain.meeting.service.response.LikedMeetingMyPageResponse;
import com.boardgo.domain.meeting.service.response.MeetingMyPageResponse;
import com.boardgo.domain.meeting.service.response.MyPageMeetingResponse;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MyPageMeetingQueryFacadeImpl implements MyPageMeetingQueryFacade {

    private final MeetingMapper meetingMapper;
    private final MeetingQueryUseCase meetingQueryUseCase;
    private final MeetingLikeQueryUseCase meetingLikeQueryUseCase;
    private final MeetingParticipantSubQueryUseCase meetingParticipantSubQueryUseCase;

    @Override
    public List<MeetingMyPageResponse> findByFilter(MyPageMeetingFilter filter, Long userId) {
        checkNullFilter(filter);
        List<MyPageMeetingResponse> myPageMeetingResponseList =
                meetingQueryUseCase.findMyPageByFilter(filter, userId);

        List<Long> meetingIdList =
                myPageMeetingResponseList.stream().map(MyPageMeetingResponse::meetingId).toList();

        return meetingMapper.toMeetingMyPageResponseList(
                myPageMeetingResponseList,
                meetingParticipantSubQueryUseCase.getMapIdAndParticipantCount(meetingIdList));
    }

    @Override
    public List<LikedMeetingMyPageResponse> findLikedMeeting(Long userId) {
        List<MeetingLikeEntity> meetingLikeEntityList = meetingLikeQueryUseCase.getByUserId(userId);

        if (meetingLikeEntityList.isEmpty()) {
            return List.of();
        }

        List<Long> meetingIdList =
                meetingLikeEntityList.stream().map(MeetingLikeEntity::getMeetingId).toList();

        return meetingQueryUseCase.findLikedMeeting(meetingIdList);
    }

    private void checkNullFilter(MyPageMeetingFilter filter) {
        if (Objects.isNull(filter)) {
            throw new CustomNullPointException("myPageMeetingFilter가 Null입니다.");
        }
    }
}
