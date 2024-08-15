package com.boardgo.domain.user.repository.response;

import com.boardgo.domain.meeting.entity.ParticipantType;

public record UserParticipantResponse(
        Long userId, String profileImage, String nickname, ParticipantType type) {}
