package com.boardgo.domain.meeting.controller.request;

import com.boardgo.common.validator.annotation.EnumValue;
import com.boardgo.domain.meeting.entity.enums.MeetingState;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MeetingOutRequest(
        @NotNull Long meetingId,
        @NotBlank
                @EnumValue(
                        enumClass = MeetingState.class,
                        message = "유효하지 않은 모임 상태입니다",
                        constraintEquals = "PROGRESS")
                String meetingState) {}
