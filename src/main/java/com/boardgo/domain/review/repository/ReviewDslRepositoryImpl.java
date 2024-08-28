package com.boardgo.domain.review.repository;

import static com.querydsl.core.group.GroupBy.groupBy;

import com.boardgo.domain.review.entity.QReviewEntity;
import com.boardgo.domain.review.repository.projection.QReviewMeetingReviewsProjection;
import com.boardgo.domain.review.repository.projection.ReviewMeetingReviewsProjection;
import com.boardgo.domain.user.entity.QUserInfoEntity;
import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class ReviewDslRepositoryImpl implements ReviewDslRepository {

    private final JPAQueryFactory queryFactory;
    private final QReviewEntity r = QReviewEntity.reviewEntity;
    private final QUserInfoEntity u = QUserInfoEntity.userInfoEntity;

    public ReviewDslRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager);
    }

    @Override
    public List<ReviewMeetingReviewsProjection> findMeetingReviews(
            Long meetingId, Long reviewerId) {
        Map<Long, ReviewMeetingReviewsProjection> meetingReviewsMap =
                queryFactory
                        .selectFrom(r)
                        .innerJoin(u)
                        .on(r.revieweeId.eq(u.id))
                        .where(r.meetingId.eq(meetingId).and(r.reviewerId.eq(reviewerId)))
                        .transform(
                                groupBy(r.id)
                                        .as(
                                                new QReviewMeetingReviewsProjection(
                                                        r.id,
                                                        u.nickName,
                                                        r.rating,
                                                        r.evaluationTags)));

        return meetingReviewsMap.keySet().stream()
                .map(meetingReviewsMap::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<List<String>> findMyEvaluationTags(Long revieweeId) {
        return queryFactory
                .select(r.evaluationTags)
                .from(r)
                .where(r.revieweeId.eq(revieweeId))
                .fetch();
    }
}
