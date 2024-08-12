package com.boardgo.domain.meeting.repository;

import com.boardgo.domain.meeting.entity.MeetingGameMatchEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MeetingGameMatchRepository
        extends JpaRepository<MeetingGameMatchEntity, Long>, MeetingGameMatchJdbcRepository {
    List<MeetingGameMatchEntity> findByMeetingId(Long meetingId);

    @Query(
            """
        SELECT GROUP_CONCAT(b.title)
        FROM MeetingGameMatchEntity mg
        INNER JOIN BoardGameEntity b ON mg.boardGameId = b.id
        WHERE mg.id = :meetingId
        GROUP BY mg.meetingId
        """)
    String findTitleByMeetingId(@Param("meetingId") Long meetingId);
}
