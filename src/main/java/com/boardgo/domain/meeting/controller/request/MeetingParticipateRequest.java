package com.boardgo.domain.meeting.controller.request;

import jakarta.validation.constraints.NotNull;

public record MeetingParticipateRequest(@NotNull Long meetingId) {}
