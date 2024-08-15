package com.boardgo.domain.user.repository;

import com.boardgo.domain.mapper.UserInfoMapper;
import com.boardgo.domain.meeting.entity.QMeetingParticipantEntity;
import com.boardgo.domain.user.entity.QUserInfoEntity;
import com.boardgo.domain.user.repository.projection.QUserParticipantProjection;
import com.boardgo.domain.user.repository.projection.UserParticipantProjection;
import com.boardgo.domain.user.repository.response.UserParticipantResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class UserDslRepositoryImpl implements UserDslRepository {
    private final JPAQueryFactory queryFactory;
    private final QUserInfoEntity u = QUserInfoEntity.userInfoEntity;
    private final QMeetingParticipantEntity mp = QMeetingParticipantEntity.meetingParticipantEntity;
    private final UserInfoMapper userInfoMapper;

    public UserDslRepositoryImpl(EntityManager entityManager, UserInfoMapper userInfoMapper) {
        this.queryFactory = new JPAQueryFactory(entityManager);
        this.userInfoMapper = userInfoMapper;
    }

    @Override
    public List<UserParticipantResponse> findByMeetingId(Long meetingId) {
        List<UserParticipantProjection> projectionList =
                queryFactory
                        .select(
                                new QUserParticipantProjection(
                                        u.id, u.profileImage, u.nickName, mp.type))
                        .from(u)
                        .innerJoin(mp)
                        .on(mp.userInfoId.eq(u.id))
                        .where(mp.meetingId.eq(meetingId))
                        .fetch();

        return projectionList.stream().map(userInfoMapper::toUserParticipantResponse).toList();
    }
}
