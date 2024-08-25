package com.boardgo.domain.meeting.controller.request;

import com.boardgo.domain.meeting.entity.enums.MyPageMeetingFilter;
import jakarta.validation.constraints.NotNull;

public record MyPageMeetingFilterRequest(@NotNull(message = "filter") MyPageMeetingFilter filter) {}
