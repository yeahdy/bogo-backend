package com.boardgo.domain.meeting.service.response;

import java.time.LocalDateTime;

public record LikedMeetingMyPageResponse(
        Long meetingId,
        String thumbnail,
        String title,
        String locationName,
        LocalDateTime meetingDatetime,
        Integer limitParticipant,
        Integer currentParticipant) {}
