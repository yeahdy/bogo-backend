package com.boardgo.domain.notification.repository;

import com.boardgo.domain.notification.entity.QNotificationEntity;
import com.boardgo.domain.notification.repository.projection.NotificationProjection;
import com.boardgo.domain.notification.repository.projection.NotificationPushProjection;
import com.boardgo.domain.user.entity.QUserInfoEntity;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class NotificationDslRepositoryImpl implements NotificationDslRepository {

    private final JPAQueryFactory queryFactory;
    private final QNotificationEntity n = QNotificationEntity.notificationEntity;
    private final QUserInfoEntity u = QUserInfoEntity.userInfoEntity;

    public NotificationDslRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<NotificationProjection> findByUserInfoId(Long userId) {
        return queryFactory
                .select(
                        Projections.constructor(
                                NotificationProjection.class,
                                n.id,
                                n.message.title,
                                n.message.content,
                                n.isRead,
                                n.pathUrl))
                .from(n)
                .where(n.isSent.eq(true).and(n.sendDateTime.before(LocalDateTime.now())))
                .orderBy(n.sendDateTime.desc())
                .fetch();
    }

    @Override
    public List<NotificationPushProjection> findNotificationPush() {
        return queryFactory
                .select(
                        Projections.constructor(
                                NotificationPushProjection.class,
                                u.userInfoStatus.pushToken,
                                n.message.title,
                                n.message.content,
                                n.pathUrl,
                                n.id))
                .from(n)
                .innerJoin(u)
                .on(n.userInfoId.eq(u.id))
                .where(
                        n.sendDateTime
                                .before(LocalDateTime.now())
                                .and(n.isSent.eq(false))
                                .and(u.userInfoStatus.pushToken.isNotNull()))
                .limit(100)
                .fetch();
    }
}
