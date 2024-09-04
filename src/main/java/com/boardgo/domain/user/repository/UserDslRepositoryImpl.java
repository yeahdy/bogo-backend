package com.boardgo.domain.user.repository;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;

import com.boardgo.common.exception.CustomNullPointException;
import com.boardgo.domain.mapper.UserInfoMapper;
import com.boardgo.domain.meeting.entity.QMeetingParticipantEntity;
import com.boardgo.domain.meeting.entity.enums.ParticipantType;
import com.boardgo.domain.user.entity.QUserInfoEntity;
import com.boardgo.domain.user.entity.QUserPrTagEntity;
import com.boardgo.domain.user.repository.projection.PersonalInfoProjection;
import com.boardgo.domain.user.repository.projection.QPersonalInfoProjection;
import com.boardgo.domain.user.repository.projection.QUserParticipantProjection;
import com.boardgo.domain.user.repository.projection.UserParticipantProjection;
import com.boardgo.domain.user.repository.response.UserParticipantResponse;
import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository
public class UserDslRepositoryImpl implements UserDslRepository {
    private final JPAQueryFactory queryFactory;
    private final QUserInfoEntity u = QUserInfoEntity.userInfoEntity;
    private final QUserPrTagEntity up = QUserPrTagEntity.userPrTagEntity;
    private final QMeetingParticipantEntity mp = QMeetingParticipantEntity.meetingParticipantEntity;
    private final UserInfoMapper userInfoMapper;

    public UserDslRepositoryImpl(EntityManager entityManager, UserInfoMapper userInfoMapper) {
        this.queryFactory = new JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager);
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
                        .where(mp.type.ne(ParticipantType.OUT).and(mp.meetingId.eq(meetingId)))
                        .fetch();

        return projectionList.stream().map(userInfoMapper::toUserParticipantResponse).toList();
    }

    @Override
    public PersonalInfoProjection findByUserInfoId(Long userId) {
        Map<Long, PersonalInfoProjection> personalInfoMap =
                queryFactory
                        .selectFrom(u)
                        .leftJoin(up)
                        .on(u.id.eq(up.userInfoId))
                        .where(u.id.eq(userId))
                        .transform(
                                groupBy(u.id)
                                        .as(
                                                new QPersonalInfoProjection(
                                                        u.email,
                                                        u.nickName,
                                                        u.profileImage,
                                                        list(up.tagName))));
        return personalInfoMap.keySet().stream()
                .map(personalInfoMap::get)
                .findFirst()
                .orElseThrow(
                        () -> {
                            throw new CustomNullPointException("회원이 존재하지 않습니다");
                        });
    }
}
