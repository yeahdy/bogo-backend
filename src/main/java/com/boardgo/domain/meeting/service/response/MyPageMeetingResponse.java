package com.boardgo.domain.meeting.service.response;

import java.time.LocalDateTime;

public record MyPageMeetingResponse(
        Long meetingId,
        Long userId,
        String title,
        String thumbnail,
        String detailAddress,
        LocalDateTime meetingDatetime,
        Integer limitParticipant) {}
