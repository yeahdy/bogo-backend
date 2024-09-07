package com.boardgo.domain.meeting.service;

import com.boardgo.domain.meeting.entity.MeetingEntity;
import java.util.List;

public interface MeetingCreateFactory {
    Long create(MeetingEntity meeting, List<Long> boardGameIdList, List<Long> boardGameGenreIdList);

    void createOnlyMatch(
            MeetingEntity meeting, List<Long> boardGameIdList, List<Long> boardGameGenreIdList);
}
