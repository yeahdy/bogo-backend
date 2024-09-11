package com.boardgo.domain.termsconditions.service;

import com.boardgo.domain.mapper.TermsConditionsMapper;
import com.boardgo.domain.termsconditions.entity.TermsConditionsEntity;
import com.boardgo.domain.termsconditions.repository.TermsConditionsRepository;
import com.boardgo.domain.termsconditions.service.response.TermsConditionsResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TermsConditionsQueryServiceV1 implements TermsConditionsQueryUseCase {
    private final TermsConditionsRepository termsConditionsRepository;
    private final TermsConditionsMapper termsConditionsMapper;

    public List<TermsConditionsEntity> getTermsConditionsEntities(List<Boolean> required) {
        return termsConditionsRepository.findAllByRequiredIn(required);
    }

    public List<TermsConditionsResponse> getTermsConditionsList(List<Boolean> required) {
        List<TermsConditionsEntity> termsConditionsEntities = getTermsConditionsEntities(required);
        return termsConditionsMapper.toTermsConditionsResponseList(termsConditionsEntities);
    }
}
