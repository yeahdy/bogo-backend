package com.boardgo.domain.meeting.service;

import com.boardgo.domain.meeting.controller.request.MeetingParticipateRequest;

public interface MeetingParticipantCommandUseCase {

    void participateMeeting(MeetingParticipateRequest participateRequest, Long userId);
}
