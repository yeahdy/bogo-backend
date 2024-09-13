package com.boardgo.domain.meeting.service;

import com.boardgo.common.exception.CustomNoSuchElementException;
import com.boardgo.domain.mapper.MeetingMapper;
import com.boardgo.domain.meeting.controller.request.MeetingSearchRequest;
import com.boardgo.domain.meeting.entity.MeetingEntity;
import com.boardgo.domain.meeting.entity.enums.MyPageMeetingFilter;
import com.boardgo.domain.meeting.repository.MeetingRepository;
import com.boardgo.domain.meeting.repository.projection.HomeMeetingDeadlineProjection;
import com.boardgo.domain.meeting.repository.projection.MeetingDetailProjection;
import com.boardgo.domain.meeting.repository.projection.MeetingSearchProjection;
import com.boardgo.domain.meeting.service.response.HomeMeetingDeadlineResponse;
import com.boardgo.domain.meeting.service.response.LikedMeetingMyPageResponse;
import com.boardgo.domain.meeting.service.response.MeetingDetailResponse;
import com.boardgo.domain.meeting.service.response.MeetingSearchResponse;
import com.boardgo.domain.meeting.service.response.MyPageMeetingResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MeetingQueryServiceV1 implements MeetingQueryUseCase {

    private final MeetingRepository meetingRepository;
    private final MeetingMapper meetingMapper;

    @Override
    public List<HomeMeetingDeadlineResponse> getMeetingDeadlines() {
        final int DEADLINE_DATE = 3;
        final int SIZE = 10;
        List<HomeMeetingDeadlineProjection> homeMeetingDeadlineProjections =
                meetingRepository.findByMeetingDateBetween(
                        LocalDateTime.now().plusMinutes(1),
                        LocalDateTime.now().plusDays(DEADLINE_DATE),
                        SIZE);
        return meetingMapper.toHomeMeetingDeadlineResponses(homeMeetingDeadlineProjections);
    }

    @Override
    public boolean isNotExistMeeting(Long meetingId) {
        return meetingRepository.existsById(meetingId);
    }

    @Override
    public MeetingEntity getMeeting(Long meetingId) {
        return meetingRepository
                .findById(meetingId)
                .orElseThrow(() -> new CustomNoSuchElementException("모임"));
    }

    @Override
    public List<MeetingSearchResponse> getMeetingSearchDtoList(
            MeetingSearchRequest searchRequest, int offset, int size) {
        List<MeetingSearchProjection> result =
                meetingRepository.getMeetingSearchDtoList(searchRequest, offset, size);
        return meetingMapper.toMeetingSearchResponseList(result);
    }

    @Override
    public Map<Long, List<String>> findGamesForMeetings(List<Long> meetingIdList) {
        return meetingRepository.findGamesForMeetings(meetingIdList);
    }

    @Override
    public Map<Long, String> findLikeStatusForMeetings(List<Long> meetingIdList, Long userId) {
        return meetingRepository.findLikeStatusForMeetings(meetingIdList, userId);
    }

    @Override
    public MeetingDetailResponse getMeetingDetailById(Long meetingId, Long userId) {
        MeetingDetailProjection result = meetingRepository.findDetailById(meetingId, userId);
        return meetingMapper.toMeetingDetailResponse(result);
    }

    @Override
    public Long getSearchTotalCount(MeetingSearchRequest searchRequest) {
        return meetingRepository.getSearchTotalCount(searchRequest);
    }

    @Override
    public Long getCreateMeetingCount(Long userId) {
        return meetingRepository.getCreateMeetingCount(userId);
    }

    @Override
    public List<MyPageMeetingResponse> findMyPageByFilter(MyPageMeetingFilter filter, Long userId) {
        return meetingMapper.toMyPageMeetingResponse(
                meetingRepository.findMyPageByFilter(filter, userId));
    }

    @Override
    public List<MeetingEntity> findByIdIn(List<Long> meetingIdList) {
        return meetingRepository.findByIdIn(meetingIdList);
    }

    @Override
    public List<LikedMeetingMyPageResponse> findLikedMeeting(List<Long> meetingIdList) {
        return meetingMapper.toLikedMeetingMyPageResponseList(
                meetingRepository.findLikedMeeting(meetingIdList));
    }
}
