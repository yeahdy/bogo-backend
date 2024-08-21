package com.boardgo.domain.meeting.repository;

import com.boardgo.domain.meeting.entity.MeetingParticipantEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MeetingParticipantRepository
        extends JpaRepository<MeetingParticipantEntity, Long> {
    List<MeetingParticipantEntity> findByMeetingId(Long meetingId);

    @Query(
            "SELECT COUNT(*) FROM MeetingParticipantEntity ep WHERE ep.type IN (:types) AND ep.userInfoId = :userId")
    Integer countByTypeAndUserInfoId(
            @Param("types") List<String> types, @Param("userId") Long userId);

    boolean existsByUserInfoIdAndMeetingId(Long userId, Long meetingId);
}
