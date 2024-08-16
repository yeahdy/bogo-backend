package com.boardgo.domain.meeting.repository;

import com.boardgo.domain.meeting.entity.MeetingParticipateWaitingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingParticipateWaitingRepository
        extends JpaRepository<MeetingParticipateWaitingEntity, Long> {}
