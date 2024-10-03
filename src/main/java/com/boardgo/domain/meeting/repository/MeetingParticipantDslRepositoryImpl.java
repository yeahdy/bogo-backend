package com.boardgo.domain.meeting.repository;

import static com.boardgo.domain.meeting.entity.enums.ParticipantType.*;

import com.boardgo.domain.meeting.entity.QMeetingParticipantEntity;
import com.boardgo.domain.meeting.entity.enums.ParticipantType;
import com.boardgo.domain.meeting.repository.projection.ReviewMeetingParticipantsProjection;
import com.boardgo.domain.user.entity.QUserInfoEntity;
import com.boardgo.domain.user.repository.projection.QUserParticipantProjection;
import com.boardgo.domain.user.repository.projection.UserParticipantProjection;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class MeetingParticipantDslRepositoryImpl implements MeetingParticipantDslRepository {
    private final JPAQueryFactory queryFactory;
    private final QMeetingParticipantEntity mp = QMeetingParticipantEntity.meetingParticipantEntity;
    private final QUserInfoEntity u = QUserInfoEntity.userInfoEntity;

    public MeetingParticipantDslRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<UserParticipantProjection> findParticipantListByMeetingId(Long meetingId) {
        return queryFactory
                .select(new QUserParticipantProjection(u.id, u.profileImage, u.nickName, mp.type))
                .from(u)
                .innerJoin(mp)
                .on(mp.userInfoId.eq(u.id))
                .where(mp.type.ne(ParticipantType.OUT).and(mp.meetingId.eq(meetingId)))
                .fetch();
    }

    @Override
    public List<ReviewMeetingParticipantsProjection> findReviewMeetingParticipants(
            List<Long> revieweeIds, Long meetingId) {
        return queryFactory
                .select(
                        Projections.constructor(
                                ReviewMeetingParticipantsProjection.class,
                                u.id.as("revieweeId"),
                                u.nickName.as("revieweeName")))
                .from(mp)
                .innerJoin(u)
                .on(mp.userInfoId.eq(u.id))
                .where(
                        mp.meetingId
                                .eq(meetingId)
                                .and(
                                        mp.type
                                                .in(LEADER, PARTICIPANT)
                                                .and(mp.userInfoId.notIn(revieweeIds))))
                .fetch();
    }

    @Override
    public List<Long> getMeetingIdByNotEqualsOut(Long userId) {
        return queryFactory
                .select(mp.meetingId)
                .from(mp)
                .where(mp.userInfoId.eq(userId).and(mp.type.ne(OUT)))
                .fetch();
    }
}
