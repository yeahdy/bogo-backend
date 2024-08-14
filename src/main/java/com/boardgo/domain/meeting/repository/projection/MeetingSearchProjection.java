package com.boardgo.domain.meeting.repository.projection;

import java.time.LocalDateTime;

public record MeetingSearchProjection(
        Long id,
        String title,
        String city,
        String county,
        String thumbnail,
        LocalDateTime meetingDate,
        Integer limitParticipant,
        String nickName,
        String genres,
        Long participantCount) {}
