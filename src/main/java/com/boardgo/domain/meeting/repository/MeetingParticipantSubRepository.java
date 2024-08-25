package com.boardgo.domain.meeting.repository;

import com.boardgo.domain.meeting.entity.MeetingParticipantSubEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingParticipantSubRepository
        extends JpaRepository<MeetingParticipantSubEntity, Long> {
    List<MeetingParticipantSubEntity> findByIdIn(List<Long> meetingIdList);
}
