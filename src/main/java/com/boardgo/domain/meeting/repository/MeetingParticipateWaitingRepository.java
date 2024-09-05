package com.boardgo.domain.meeting.repository;

import com.boardgo.domain.meeting.entity.MeetingParticipateWaitingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MeetingParticipateWaitingRepository
        extends JpaRepository<MeetingParticipateWaitingEntity, Long> {

    @Modifying
    @Query("DELETE FROM MeetingParticipateWaitingEntity mpw " + "WHERE mpw.meetingId = :meetingId")
    int deleteAllInBatchByMeetingId(@Param("meetingId") Long meetingId);
}
