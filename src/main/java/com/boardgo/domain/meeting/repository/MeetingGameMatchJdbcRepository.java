package com.boardgo.domain.meeting.repository;

import java.util.List;

public interface MeetingGameMatchJdbcRepository {
    void bulkInsert(List<Long> gameIdList, Long meetingId);
}
