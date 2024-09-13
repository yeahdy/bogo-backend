package com.boardgo.domain.meeting.service.facade;

import com.boardgo.domain.meeting.entity.enums.MyPageMeetingFilter;
import com.boardgo.domain.meeting.service.response.LikedMeetingMyPageResponse;
import com.boardgo.domain.meeting.service.response.MeetingMyPageResponse;
import java.util.List;

public interface MyPageMeetingQueryFacade {
    List<MeetingMyPageResponse> findByFilter(MyPageMeetingFilter filter, Long userId);

    List<LikedMeetingMyPageResponse> findLikedMeeting(Long userId);
}
