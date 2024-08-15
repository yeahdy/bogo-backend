package com.boardgo.domain.user.repository.projection;

import com.boardgo.domain.meeting.entity.ParticipantType;
import com.querydsl.core.annotations.QueryProjection;

public record UserParticipantProjection(
        Long userId, String profileImage, String nickname, ParticipantType type) {
    @QueryProjection
    public UserParticipantProjection {}
}
