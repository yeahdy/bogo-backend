package com.boardgo.domain.meeting.repository.projection;

import com.boardgo.domain.meeting.entity.enums.MeetingState;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;

public record MeetingDetailProjection(
        Long meetingId,
        String userNickName,
        Long userId,
        LocalDateTime meetingDatetime,
        String thumbnail,
        String title,
        String content,
        String longitude,
        String latitude,
        String city,
        String county,
        String locationName,
        String detailAddress,
        Integer limitParticipant,
        MeetingState state,
        Integer shareCount,
        Long viewCount) {
    @QueryProjection
    public MeetingDetailProjection {}
}
