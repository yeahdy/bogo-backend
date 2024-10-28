package com.boardgo.domain.termsconditions.service;

import com.boardgo.domain.termsconditions.entity.UserTermsConditionsEntity;
import com.boardgo.domain.termsconditions.entity.enums.TermsConditionsType;

public interface UserTermsConditionsQueryUseCase {
    boolean existsUser(Long userInfoId);

    UserTermsConditionsEntity getUserTermsConditionsEntity(
            Long userInfoId, TermsConditionsType termsConditionsType);
}
