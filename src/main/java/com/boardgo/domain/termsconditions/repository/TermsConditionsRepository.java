package com.boardgo.domain.termsconditions.repository;

import com.boardgo.domain.termsconditions.entity.TermsConditionsEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TermsConditionsRepository extends JpaRepository<TermsConditionsEntity, Long> {
    List<TermsConditionsEntity> findAllByRequiredIn(List<Boolean> required);
}
