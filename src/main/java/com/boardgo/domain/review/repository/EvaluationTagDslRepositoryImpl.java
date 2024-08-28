package com.boardgo.domain.review.repository;

import com.boardgo.domain.review.entity.EvaluationType;
import com.boardgo.domain.review.entity.QEvaluationTagEntity;
import com.boardgo.domain.review.repository.projection.MyEvaluationTagProjection;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Repository;

@Repository
public class EvaluationTagDslRepositoryImpl implements EvaluationTagDslRepository {

    private final JPAQueryFactory queryFactory;
    private final QEvaluationTagEntity et = QEvaluationTagEntity.evaluationTagEntity;

    public EvaluationTagDslRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<MyEvaluationTagProjection> findEvaluationTagsById(
            EvaluationType evaluationType, Set<Long> evaluationTags) {
        return queryFactory
                .select(
                        Projections.constructor(
                                MyEvaluationTagProjection.class, et.id, et.tagPhrase))
                .from(et)
                .where(et.evaluationType.eq(evaluationType).and(et.id.in(evaluationTags)))
                .fetch();
    }
}
