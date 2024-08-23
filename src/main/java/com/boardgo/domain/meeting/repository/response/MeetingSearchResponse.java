package com.boardgo.domain.meeting.repository.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record MeetingSearchResponse(
        Long id,
        String title,
        String city,
        String county,
        String thumbnail,
        Long viewCount,
        String likeStatus,
        LocalDateTime meetingDate,
        Integer limitParticipant,
        String nickName,
        List<String> games,
        Set<String> tags,
        Integer participantCount) {}
