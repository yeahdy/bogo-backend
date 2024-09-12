package com.boardgo.domain.meeting.repository.projection;

import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDateTime;

public record MyPageMeetingProjection(
        Long meetingId,
        Long userId,
        String title,
        String thumbnail,
        String detailAddress,
        LocalDateTime meetingDatetime,
        Integer limitParticipant) {

    @QueryProjection
    public MyPageMeetingProjection {}
}
