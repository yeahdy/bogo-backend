package com.boardgo.domain.meeting.repository.projection;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;

public record LikedMeetingMyPageProjection(
        Long meetingId,
        String thumbnail,
        String title,
        String locationName,
        LocalDateTime meetingDatetime,
        Integer limitParticipant,
        Integer currentParticipant) {
    @QueryProjection
    public LikedMeetingMyPageProjection {}
}
