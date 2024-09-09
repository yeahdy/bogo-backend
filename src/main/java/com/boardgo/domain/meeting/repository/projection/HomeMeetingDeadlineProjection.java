package com.boardgo.domain.meeting.repository.projection;

import java.time.LocalDateTime;

public record HomeMeetingDeadlineProjection(
        Long meetingId,
        String thumbnail,
        String title,
        String city,
        String county,
        LocalDateTime meetingDatetime) {}
