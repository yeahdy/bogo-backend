package com.boardgo.domain.meeting.repository.response;

import com.boardgo.domain.meeting.repository.projection.MeetingSearchProjection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record MeetingSearchResponse(
        Long id,
        String title,
        String city,
        String county,
        String thumbnail,
        LocalDateTime meetingDate,
        Integer limitParticipant,
        String nickName,
        List<String> games,
        Set<String> tags,
        Long participantCount) {
    public MeetingSearchResponse(MeetingSearchProjection queryDto, List<String> games) {
        this(
                queryDto.id(),
                queryDto.title(),
                queryDto.city(),
                queryDto.county(),
                queryDto.thumbnail(),
                queryDto.meetingDate(),
                queryDto.limitParticipant(),
                queryDto.nickName(),
                games,
                Set.of(queryDto.genres().split(",")),
                queryDto.participantCount());
    }
}
