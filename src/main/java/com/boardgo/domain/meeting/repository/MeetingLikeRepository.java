package com.boardgo.domain.meeting.repository;

import com.boardgo.domain.meeting.entity.MeetingLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingLikeRepository extends JpaRepository<MeetingLike, Long> {}
