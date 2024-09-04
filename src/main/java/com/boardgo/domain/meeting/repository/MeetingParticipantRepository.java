package com.boardgo.domain.meeting.repository;

import com.boardgo.domain.meeting.entity.MeetingParticipantEntity;
import com.boardgo.domain.meeting.entity.enums.ParticipantType;
import com.boardgo.domain.meeting.repository.projection.ParticipationCountProjection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MeetingParticipantRepository
        extends JpaRepository<MeetingParticipantEntity, Long>, MeetingParticipantDslRepository {
    List<MeetingParticipantEntity> findByMeetingId(Long meetingId);

    Optional<MeetingParticipantEntity> findByMeetingIdAndUserInfoIdAndType(
            Long meetingId, Long userId, ParticipantType type);

    Long deleteAllByMeetingId(Long meetingId);

    @Query(
            "SELECT COUNT(*) FROM MeetingParticipantEntity mp WHERE mp.type IN (:types) AND mp.userInfoId = :userId")
    Integer countByTypeAndUserInfoId(
            @Param("types") List<ParticipantType> types, @Param("userId") Long userId);

    boolean existsByUserInfoIdAndMeetingId(Long userId, Long meetingId);

    @Query(
            "SELECT mp.meetingId AS meetingId, COUNT(*) AS participationCount "
                    + "FROM MeetingParticipantEntity mp "
                    + "WHERE mp.meetingId IN (:meetingIds) AND mp.type IN (:types) "
                    + "GROUP BY mp.meetingId")
    List<ParticipationCountProjection> countMeetingParticipation(
            @Param("meetingIds") Set<Long> meetingIds, @Param("types") List<ParticipantType> types);

    @Query(
            "SELECT COUNT(*) "
                    + "FROM MeetingParticipantEntity mp "
                    + "WHERE mp.meetingId = :meetingId AND mp.userInfoId IN (:userIds)")
    Long countMeetingParticipant(
            @Param("meetingId") Long meetingId, @Param("userIds") List<Long> userIds);
}
