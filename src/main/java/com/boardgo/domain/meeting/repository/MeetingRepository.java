package com.boardgo.domain.meeting.repository;

import com.boardgo.domain.meeting.entity.MeetingEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MeetingRepository
        extends JpaRepository<MeetingEntity, Long>, MeetingDslRepository {
    List<MeetingEntity> findByIdIn(List<Long> meetingIdList);

    @Modifying
    @Query("UPDATE MeetingEntity m SET m.viewCount = m.viewCount + 1 WHERE m.id = :meetingId")
    void incrementViewCount(@Param("meetingId") Long meetingId);
}
