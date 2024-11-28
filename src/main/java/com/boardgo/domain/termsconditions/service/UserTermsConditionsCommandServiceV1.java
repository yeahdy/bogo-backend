package com.boardgo.domain.termsconditions.service;

import com.boardgo.domain.termsconditions.entity.UserTermsConditionsEntity;
import com.boardgo.domain.termsconditions.entity.enums.TermsConditionsType;
import com.boardgo.domain.termsconditions.repository.UserTermsConditionsRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserTermsConditionsCommandServiceV1 implements UserTermsConditionsCommandUseCase {
    private final UserTermsConditionsRepository userTermsConditionsRepository;

    @Override
    public void createAll(List<UserTermsConditionsEntity> userTermsConditionsEntities) {
        userTermsConditionsRepository.saveAll(userTermsConditionsEntities);
    }

    @Override
    public void updatePushTermsCondition(Long userId) {
        UserTermsConditionsEntity userTermsConditionsEntity =
                userTermsConditionsRepository.findByUserInfoIdAndTermsConditionsType(
                        userId, TermsConditionsType.PUSH);
        if (userTermsConditionsEntity.getAgreement()) {
            userTermsConditionsEntity.updateAgreement(Boolean.FALSE);
        } else {
            userTermsConditionsEntity.updateAgreement(Boolean.TRUE);
        }
    }
}
