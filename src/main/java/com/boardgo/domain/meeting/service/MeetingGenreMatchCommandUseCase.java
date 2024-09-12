package com.boardgo.domain.meeting.service;

import java.util.List;

public interface MeetingGenreMatchCommandUseCase {
    void bulkInsert(List<Long> genreIdList, Long meetingId);

    void deleteByMeetingId(Long meetingId);
}
