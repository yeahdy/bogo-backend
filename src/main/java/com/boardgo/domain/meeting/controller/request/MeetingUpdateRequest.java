package com.boardgo.domain.meeting.controller.request;

import com.boardgo.common.validator.annotation.AllowedValues;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;

public record MeetingUpdateRequest(
        @NotNull Long id,
        @NotEmpty(message = "content") String content,
        @NotEmpty(message = "type") @AllowedValues(values = {"FREE", "ACCEPT"}) String type,
        @Positive(message = "limitParticipant") Integer limitParticipant,
        @NotEmpty(message = "title") String title,
        @NotEmpty String city,
        @NotEmpty String county,
        @NotEmpty String latitude,
        @NotEmpty String longitude,
        @NotEmpty String detailAddress,
        @NotEmpty String locationName,
        @Future @NotNull @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime meetingDatetime,
        /* 보드게임의 id들 */
        @NotNull(message = "boardGameIdList") List<Long> boardGameIdList,
        /* GenreId */
        @NotNull(message = "genreIdList") List<Long> genreIdList) {}
