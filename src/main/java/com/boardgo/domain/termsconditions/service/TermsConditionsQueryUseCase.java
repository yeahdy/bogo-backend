package com.boardgo.domain.termsconditions.service;

import com.boardgo.domain.termsconditions.entity.TermsConditionsEntity;
import com.boardgo.domain.termsconditions.service.response.TermsConditionsResponse;
import java.util.List;

public interface TermsConditionsQueryUseCase {
    List<TermsConditionsEntity> getTermsConditionsEntities(List<Boolean> required);

    List<TermsConditionsResponse> getTermsConditionsList(List<Boolean> required);
}
