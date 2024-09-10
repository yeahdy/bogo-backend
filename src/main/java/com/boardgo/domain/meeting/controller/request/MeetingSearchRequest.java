package com.boardgo.domain.meeting.controller.request;

import com.boardgo.common.validator.annotation.AllowedValues;
import java.time.LocalDateTime;

public record MeetingSearchRequest(
        Long count,
        String tag,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String searchWord,
        @AllowedValues(values = {"TITLE", "CONTENT", "ALL"}) String searchType,
        String city,
        String county,
        Integer page,
        Integer size,
        @AllowedValues(values = {"COMPLETE"}) String state,
        @AllowedValues(values = {"MEETING_DATE", "PARTICIPANT_COUNT"}) String sortBy) {}
