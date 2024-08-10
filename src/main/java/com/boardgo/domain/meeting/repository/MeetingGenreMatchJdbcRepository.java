package com.boardgo.domain.meeting.repository;

import java.util.List;

public interface MeetingGenreMatchJdbcRepository {
    void bulkInsert(List<Long> genreIdList, Long meetingId);
}
