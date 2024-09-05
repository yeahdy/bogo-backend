package com.boardgo.domain.meeting.repository;

import com.boardgo.domain.meeting.entity.MeetingLikeEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MeetingLikeRepository
        extends JpaRepository<MeetingLikeEntity, Long>, MeetingLikeJdbcRepository {
    List<MeetingLikeEntity> findByUserId(Long userId);

    List<MeetingLikeEntity> findByMeetingId(Long meetingId);

    Optional<MeetingLikeEntity> findByUserIdAndMeetingId(Long userId, Long meetingId);

    boolean existsByUserIdAndMeetingId(Long userId, Long meetingId);

    Long deleteByUserIdAndMeetingId(Long userId, Long meetingId);

    @Modifying
    @Query("DELETE FROM MeetingLikeEntity ml " + "WHERE ml.meetingId = :meetingId")
    int deleteAllInBatchByMeetingId(@Param("meetingId") Long meetingId);
}
