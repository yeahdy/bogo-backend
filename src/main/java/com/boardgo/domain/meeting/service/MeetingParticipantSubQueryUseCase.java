package com.boardgo.domain.meeting.service;

import com.boardgo.domain.meeting.entity.MeetingParticipantSubEntity;
import java.util.List;
import java.util.Map;

public interface MeetingParticipantSubQueryUseCase {

    MeetingParticipantSubEntity getByMeetingId(Long meetingId);

    Map<Long, MeetingParticipantSubEntity> getMapIdAndParticipantCount(List<Long> meetingIdList);
}
