package com.boardgo.domain.meeting.service;

import com.boardgo.common.exception.CustomNullPointException;
import com.boardgo.common.utils.SecurityUtils;
import com.boardgo.domain.mapper.MeetingMapper;
import com.boardgo.domain.meeting.entity.MeetingParticipantSubEntity;
import com.boardgo.domain.meeting.entity.enums.MyPageMeetingFilter;
import com.boardgo.domain.meeting.repository.MeetingParticipantSubRepository;
import com.boardgo.domain.meeting.repository.MeetingRepository;
import com.boardgo.domain.meeting.repository.projection.MyPageMeetingProjection;
import com.boardgo.domain.meeting.service.response.MeetingMyPageResponse;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MyPageMeetingQueryServiceV1 implements MyPageMeetingQueryUseCase {

    private final MeetingMapper meetingMapper;
    private final MeetingRepository meetingRepository;
    private final MeetingParticipantSubRepository meetingParticipantSubRepository;

    @Override
    public List<MeetingMyPageResponse> findByFilter(MyPageMeetingFilter filter) {
        checkNullFilter(filter);
        List<MyPageMeetingProjection> myPageMeetingProjectionList =
                meetingRepository.findMyPageByFilter(filter, SecurityUtils.currentUserId());

        List<Long> meetingIdList =
                myPageMeetingProjectionList.stream()
                        .map(MyPageMeetingProjection::meetingId)
                        .toList();

        return meetingMapper.toMeetingMyPageResponseList(
                myPageMeetingProjectionList, getLongMeetingParticipantSubEntityMap(meetingIdList));
    }

    private Map<Long, MeetingParticipantSubEntity> getLongMeetingParticipantSubEntityMap(
            List<Long> meetingIdList) {
        List<MeetingParticipantSubEntity> meetingParticipantSubEntityList =
                meetingParticipantSubRepository.findByIdIn(meetingIdList);

        return meetingParticipantSubEntityList.stream()
                .collect(
                        Collectors.toMap(
                                MeetingParticipantSubEntity::getId,
                                Function.identity(),
                                (existing, replacement) -> existing));
    }

    private void checkNullFilter(MyPageMeetingFilter filter) {
        if (Objects.isNull(filter)) {
            throw new CustomNullPointException("myPageMeetingFilter가 Null입니다.");
        }
    }
}
