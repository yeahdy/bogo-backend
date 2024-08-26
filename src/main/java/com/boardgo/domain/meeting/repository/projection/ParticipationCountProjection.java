package com.boardgo.domain.meeting.repository.projection;

public interface ParticipationCountProjection {
    Long getMeetingId();

    Integer getParticipationCount();
}
