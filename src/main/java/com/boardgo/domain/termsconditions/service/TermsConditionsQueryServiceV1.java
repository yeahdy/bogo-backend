package com.boardgo.domain.termsconditions.service;

import com.boardgo.domain.termsconditions.entity.TermsConditionsEntity;
import com.boardgo.domain.termsconditions.repository.TermsConditionsRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TermsConditionsQueryServiceV1 implements TermsConditionsQueryUseCase {
    private final TermsConditionsRepository termsConditionsRepository;

    public List<TermsConditionsEntity> getTermsConditions(List<Boolean> required) {
        return termsConditionsRepository.findAllByRequiredIn(required);
    }
}
