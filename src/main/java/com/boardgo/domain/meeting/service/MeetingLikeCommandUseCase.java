package com.boardgo.domain.meeting.service;

import java.util.List;

public interface MeetingLikeCommandUseCase {

    void createMany(List<Long> meetingIdList);

    void deleteByUserIdAndMeetingId(Long userId, Long meetingId);

    void deleteByMeetingId(Long meetingId);
}
