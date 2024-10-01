package com.boardgo.domain.meeting.controller.request;

import java.time.LocalDateTime;
import java.util.List;

import com.boardgo.common.validator.annotation.AllowedValues;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

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
		boolean isDeleteThumbnail,
        @Future @NotNull @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime meetingDatetime,
        List<Long> boardGameIdList) {}
