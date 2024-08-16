package com.boardgo.domain.meeting.repository;

import com.boardgo.domain.meeting.entity.MeetingParticipantSubEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingParticipantSubRepository
        extends JpaRepository<MeetingParticipantSubEntity, Long> {}
