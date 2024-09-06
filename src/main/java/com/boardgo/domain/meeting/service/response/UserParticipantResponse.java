package com.boardgo.domain.meeting.service.response;

import com.boardgo.domain.meeting.entity.enums.ParticipantType;

public record UserParticipantResponse(
        Long userId, String profileImage, String nickname, ParticipantType type) {}
