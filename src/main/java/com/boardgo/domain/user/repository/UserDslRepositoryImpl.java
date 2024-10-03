package com.boardgo.domain.user.repository;

import static com.querydsl.core.group.GroupBy.*;

import com.boardgo.common.exception.CustomNullPointException;
import com.boardgo.domain.user.entity.QUserInfoEntity;
import com.boardgo.domain.user.entity.QUserPrTagEntity;
import com.boardgo.domain.user.repository.projection.PersonalInfoProjection;
import com.boardgo.domain.user.repository.projection.QPersonalInfoProjection;
import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository
public class UserDslRepositoryImpl implements UserDslRepository {
    private final JPAQueryFactory queryFactory;
    private final QUserInfoEntity u = QUserInfoEntity.userInfoEntity;
    private final QUserPrTagEntity up = QUserPrTagEntity.userPrTagEntity;

    public UserDslRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager);
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
