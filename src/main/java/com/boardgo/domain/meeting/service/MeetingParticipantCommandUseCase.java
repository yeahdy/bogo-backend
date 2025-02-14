package com.boardgo.domain.meeting.service;

import com.boardgo.domain.meeting.controller.request.MeetingParticipateRequest;
import com.boardgo.domain.meeting.entity.MeetingParticipantEntity;

public interface MeetingParticipantCommandUseCase {

    void participateMeeting(MeetingParticipateRequest participateRequest, Long userId);

    void outMeeting(Long meetingId, Long userId);

    void create(MeetingParticipantEntity meetingParticipantEntity);

    void deleteByMeetingId(Long meetingId);
}
