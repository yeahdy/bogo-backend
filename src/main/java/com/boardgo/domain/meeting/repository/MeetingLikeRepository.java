package com.boardgo.domain.meeting.repository;

import com.boardgo.domain.meeting.entity.MeetingLikeEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingLikeRepository
        extends JpaRepository<MeetingLikeEntity, Long>, MeetingLikeJdbcRepository {
    List<MeetingLikeEntity> findByUserId(Long userId);

    Optional<MeetingLikeEntity> findByUserIdAndMeetingId(Long userId, Long meetingId);

    boolean existsByUserIdAndMeetingId(Long userId, Long meetingId);

    Long deleteByUserIdAndMeetingId(Long userId, Long meetingId);
}
