package com.boardgo.domain.meeting.repository.projection;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;

public record MeetingSearchProjection(
        Long id,
        String title,
        String city,
        String county,
        String thumbnail,
        Long viewCount,
        LocalDateTime meetingDate,
        Integer limitParticipant,
        String nickName,
        String genres,
        Integer participantCount) {
    @QueryProjection
    public MeetingSearchProjection {}
}
