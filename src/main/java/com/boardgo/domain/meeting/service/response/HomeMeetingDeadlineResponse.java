package com.boardgo.domain.meeting.service.response;

import java.time.LocalDateTime;

public record HomeMeetingDeadlineResponse(
        Long meetingId,
        String thumbnail,
        String title,
        String city,
        String county,
        LocalDateTime meetingDatetime) {}
