package com.boardgo.domain.meeting.repository.projection;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;

public record MeetingReviewProjection(
        Long id,
        String title,
        String thumbnail,
        String city,
        String county,
        LocalDateTime meetingDatetime) {
    @QueryProjection
    public MeetingReviewProjection {}
}
