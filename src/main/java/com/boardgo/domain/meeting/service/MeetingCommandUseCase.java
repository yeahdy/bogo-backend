package com.boardgo.domain.meeting.service;

import com.boardgo.domain.meeting.entity.MeetingEntity;

public interface MeetingCommandUseCase {
    Long create(MeetingEntity meeting);

    void deleteById(Long meetingId);
}
