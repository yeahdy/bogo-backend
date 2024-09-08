package com.boardgo.domain.termsconditions.repository;

import com.boardgo.domain.termsconditions.entity.UserTermsConditionsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTermsConditionsRepository
        extends JpaRepository<UserTermsConditionsEntity, Long> {}
