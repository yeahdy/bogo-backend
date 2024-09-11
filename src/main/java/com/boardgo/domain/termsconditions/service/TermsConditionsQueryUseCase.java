package com.boardgo.domain.termsconditions.service;

import com.boardgo.domain.termsconditions.entity.TermsConditionsEntity;
import java.util.List;

public interface TermsConditionsQueryUseCase {
    List<TermsConditionsEntity> getTermsConditions(List<Boolean> required);
}
