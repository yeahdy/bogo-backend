package com.boardgo.domain.meeting.service;

import com.boardgo.domain.meeting.entity.enums.MyPageMeetingFilter;
import com.boardgo.domain.meeting.service.response.LikedMeetingMyPageResponse;
import com.boardgo.domain.meeting.service.response.MeetingMyPageResponse;
import java.util.List;

public interface MyPageMeetingQueryUseCase {
    List<MeetingMyPageResponse> findByFilter(MyPageMeetingFilter filter);

    List<LikedMeetingMyPageResponse> findLikedMeeting();
}
