package com.boardgo.domain.meeting.repository;

import com.boardgo.domain.meeting.entity.MeetingGameMatchEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingGameMatchRepository
        extends JpaRepository<MeetingGameMatchEntity, Long>, MeetingGameMatchJdbcRepository {
    List<MeetingGameMatchEntity> findByMeetingId(Long meetingId);
}
