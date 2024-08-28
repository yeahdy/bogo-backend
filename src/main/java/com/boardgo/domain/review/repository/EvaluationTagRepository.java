package com.boardgo.domain.review.repository;

import com.boardgo.domain.review.entity.EvaluationTagEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EvaluationTagRepository
        extends JpaRepository<EvaluationTagEntity, Long>, EvaluationTagDslRepository {

    List<EvaluationTagEntity> findAllByOrderById();
}
