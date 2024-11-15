package com.boardgo.domain.meeting.repository;

import static com.boardgo.domain.meeting.entity.enums.ParticipantType.LEADER;
import static com.boardgo.domain.meeting.entity.enums.ParticipantType.OUT;
import static com.boardgo.domain.meeting.entity.enums.ParticipantType.PARTICIPANT;

import com.boardgo.config.JPASQLQueryFactory;
import com.boardgo.domain.meeting.entity.QMeetingParticipantEntity;
import com.boardgo.domain.meeting.entity.enums.ParticipantType;
import com.boardgo.domain.meeting.repository.projection.MeetingParticipantsCountProjection;
import com.boardgo.domain.meeting.repository.projection.ReviewMeetingParticipantsProjection;
import com.boardgo.domain.user.entity.QUserInfoEntity;
import com.boardgo.domain.user.repository.projection.QUserParticipantProjection;
import com.boardgo.domain.user.repository.projection.UserParticipantProjection;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.sql.SQLExpressions;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MeetingParticipantDslRepositoryImpl implements MeetingParticipantDslRepository {
    private final JPAQueryFactory queryFactory;
    private final JPASQLQueryFactory jpasqlQueryFactory;
    private final QMeetingParticipantEntity mp = QMeetingParticipantEntity.meetingParticipantEntity;
    private final QUserInfoEntity u = QUserInfoEntity.userInfoEntity;

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
    public List<ReviewMeetingParticipantsProjection> findMeetingParticipantsToReview(
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

    /**
     * 참여한 모임 별 모임 참여자 수
     *
     * @param participantCount null이 아닐 경우 모임 참여자 수가 일치하는 모임 조회
     * @return Map<모임Id, 모임참여자수>
     */
    @Override
    public List<MeetingParticipantsCountProjection> countMeetingParticipantsByUserInfoId(
            Long userId, Long participantCount) {
        StringPath mpSub = Expressions.stringPath("mp_sub");

        return jpasqlQueryFactory
                .createQuery()
                .select(
                        Projections.constructor(
                                MeetingParticipantsCountProjection.class,
                                mp.meetingId,
                                mp.meetingId.count().as("meetingParticipantsCount")))
                .from(mp)
                .innerJoin(
                        SQLExpressions.select(mp.meetingId, mp.type)
                                .from(mp)
                                .where(mp.userInfoId.eq(userId)),
                        mpSub)
                .on(mp.meetingId.eq(Expressions.numberPath(Long.class, mpSub, "meeting_id")))
                .where(mp.type.ne(ParticipantType.OUT))
                .groupBy(mp.meetingId)
                .having(existParticipantCount(participantCount))
                .fetch();
    }

    public BooleanExpression existParticipantCount(Long participantCount) {
        return participantCount != null ? mp.meetingId.count().eq(participantCount) : null;
    }
}
