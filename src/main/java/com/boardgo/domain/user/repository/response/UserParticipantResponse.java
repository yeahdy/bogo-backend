package com.boardgo.domain.user.repository.response;

import com.boardgo.domain.meeting.entity.enums.ParticipantType;

public record UserParticipantResponse(
        Long userId, String profileImage, String nickname, ParticipantType type) {}
