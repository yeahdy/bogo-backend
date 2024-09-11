package com.boardgo.domain.termsconditions.service;

import com.boardgo.domain.termsconditions.entity.UserTermsConditionsEntity;
import java.util.List;

public interface UserTermsConditionsCommandUseCase {
    void createAll(List<UserTermsConditionsEntity> userTermsConditionsEntities);
}
