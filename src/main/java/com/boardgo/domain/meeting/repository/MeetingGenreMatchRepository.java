package com.boardgo.domain.meeting.repository;

import com.boardgo.domain.meeting.entity.MeetingGenreMatchEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MeetingGenreMatchRepository
        extends JpaRepository<MeetingGenreMatchEntity, Long>, MeetingGenreMatchJdbcRepository {

    List<MeetingGenreMatchEntity> findByMeetingId(Long meetingId);

    @Query(
            "SELECT GROUP_CONCAT(g.genre) AS genres "
                    + "FROM MeetingGenreMatchEntity mg "
                    + "INNER JOIN BoardGameGenreEntity g ON mg.boardGameGenreId = g.id "
                    + "WHERE mg.meetingId = :meetingId "
                    + "GROUP BY mg.meetingId")
    String findGenresByMeetingId(@Param("meetingId") Long meetingId);

    Long deleteAllByMeetingId(Long meetingId);
}
