package com.boardgo.domain.meeting.service;

import com.boardgo.domain.meeting.entity.MeetingParticipantSubEntity;

public interface MeetingParticipantSubQueryUseCase {

    MeetingParticipantSubEntity getByMeetingId(Long meetingId);
}
