package com.boardgo.domain.meeting.repository;

import com.boardgo.domain.meeting.entity.MeetingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingRepository extends JpaRepository<MeetingEntity, Long> {}
