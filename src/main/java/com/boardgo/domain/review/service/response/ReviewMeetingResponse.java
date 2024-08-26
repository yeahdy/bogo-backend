package com.boardgo.domain.review.service.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public record ReviewMeetingResponse(
        @JsonProperty("meetingId") Long id,
        String title,
        String thumbnail,
        String city,
        String county,
        @JsonProperty("meetingDate") LocalDateTime meetingDatetime) {}
