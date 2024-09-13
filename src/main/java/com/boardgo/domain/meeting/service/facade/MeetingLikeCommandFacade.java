package com.boardgo.domain.meeting.service.facade;

import java.util.List;

public interface MeetingLikeCommandFacade {
    void createMany(List<Long> meetingIdList, Long userId);
}
