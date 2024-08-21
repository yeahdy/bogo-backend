package com.boardgo.domain.meeting.repository;

import com.boardgo.domain.meeting.entity.MeetingParticipantEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeetingParticipantRepository
        extends JpaRepository<MeetingParticipantEntity, Long> {
    List<MeetingParticipantEntity> findByMeetingId(Long meetingId);

    boolean existsByUserInfoIdAndMeetingId(Long userId, Long meetingId);
}
