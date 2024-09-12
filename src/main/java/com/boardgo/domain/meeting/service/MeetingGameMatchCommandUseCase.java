package com.boardgo.domain.meeting.service;

import java.util.List;

public interface MeetingGameMatchCommandUseCase {
    void bulkInsert(List<Long> boardGameIdList, Long meetingId);

    void deleteByMeetingId(Long meetingId);
}
