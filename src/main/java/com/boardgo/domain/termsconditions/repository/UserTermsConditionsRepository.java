package com.boardgo.domain.termsconditions.repository;

import com.boardgo.domain.termsconditions.entity.UserTermsConditionsEntity;
import com.boardgo.domain.termsconditions.entity.enums.TermsConditionsType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTermsConditionsRepository
        extends JpaRepository<UserTermsConditionsEntity, Long> {
    boolean existsByUserInfoId(Long userInfoId);

    List<UserTermsConditionsEntity> findByUserInfoId(Long userInfoId);

    UserTermsConditionsEntity findByUserInfoIdAndTermsConditionsType(
            Long userInfoId, TermsConditionsType termsConditionsType);
}
