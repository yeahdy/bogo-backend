package com.boardgo.domain.termsconditions.repository;

import com.boardgo.domain.termsconditions.entity.TermsConditionsHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TermsConditionsHistoryRepository
        extends JpaRepository<TermsConditionsHistoryEntity, Long> {}
