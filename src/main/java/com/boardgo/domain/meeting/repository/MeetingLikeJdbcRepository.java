package com.boardgo.domain.meeting.repository;

import java.util.List;

public interface MeetingLikeJdbcRepository {
    void bulkInsert(List<Long> meetingIdList, Long userId);
}
