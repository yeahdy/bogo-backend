package com.boardgo.domain.meeting.repository;

import com.boardgo.domain.meeting.entity.MeetingLikeEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingLikeRepository
        extends JpaRepository<MeetingLikeEntity, Long>, MeetingLikeJdbcRepository {
    List<MeetingLikeEntity> findByUserId(Long userId);
}
