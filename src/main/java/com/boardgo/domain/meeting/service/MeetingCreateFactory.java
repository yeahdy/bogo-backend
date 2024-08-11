package com.boardgo.domain.meeting.service;

import com.boardgo.domain.meeting.entity.MeetingEntity;
import java.util.List;

public interface MeetingCreateFactory {
    Long create(
            MeetingEntity meeting,
            Long userId,
            List<Long> boardGameIdList,
            List<Long> boardGameGenreIdList);
}
