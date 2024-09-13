package com.boardgo.domain.meeting.service;

import com.boardgo.domain.meeting.controller.request.MeetingSearchRequest;
import com.boardgo.domain.meeting.entity.MeetingEntity;
import com.boardgo.domain.meeting.entity.enums.MyPageMeetingFilter;
import com.boardgo.domain.meeting.service.response.HomeMeetingDeadlineResponse;
import com.boardgo.domain.meeting.service.response.LikedMeetingMyPageResponse;
import com.boardgo.domain.meeting.service.response.MeetingDetailResponse;
import com.boardgo.domain.meeting.service.response.MeetingSearchResponse;
import com.boardgo.domain.meeting.service.response.MyPageMeetingResponse;
import java.util.List;
import java.util.Map;

public interface MeetingQueryUseCase {

    List<HomeMeetingDeadlineResponse> getMeetingDeadlines();

    boolean isNotExistMeeting(Long meetingId);

    MeetingEntity getMeeting(Long meetingId);

    List<MeetingSearchResponse> getMeetingSearchDtoList(
            MeetingSearchRequest searchRequest, int offset, int size);

    Map<Long, List<String>> findGamesForMeetings(List<Long> meetingIdList);

    Map<Long, String> findLikeStatusForMeetings(List<Long> meetingIdList, Long userId);

    MeetingDetailResponse getMeetingDetailById(Long meetingId, Long userId);

    Long getSearchTotalCount(MeetingSearchRequest searchRequest);

    Long getCreateMeetingCount(Long userId);

    List<MyPageMeetingResponse> findMyPageByFilter(MyPageMeetingFilter filter, Long userId);

    List<MeetingEntity> findByIdIn(List<Long> meetingIdList);

    List<LikedMeetingMyPageResponse> findLikedMeeting(List<Long> meetingIdList);
}
