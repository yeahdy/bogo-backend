package com.boardgo.domain.review.repository;

import com.boardgo.domain.review.entity.EvaluationType;
import com.boardgo.domain.review.repository.projection.MyEvaluationTagProjection;
import java.util.List;
import java.util.Set;

public interface EvaluationTagDslRepository {

    List<MyEvaluationTagProjection> findEvaluationTagsById(
            EvaluationType evaluationType, Set<Long> evaluationTags);
}
