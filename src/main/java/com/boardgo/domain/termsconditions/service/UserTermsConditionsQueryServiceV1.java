package com.boardgo.domain.termsconditions.service;

import com.boardgo.common.exception.CustomNullPointException;
import com.boardgo.domain.termsconditions.entity.UserTermsConditionsEntity;
import com.boardgo.domain.termsconditions.entity.enums.TermsConditionsType;
import com.boardgo.domain.termsconditions.repository.UserTermsConditionsRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserTermsConditionsQueryServiceV1 implements UserTermsConditionsQueryUseCase {
    private final UserTermsConditionsRepository userTermsConditionsRepository;

    @Override
    public boolean existsUser(Long userInfoId) {
        return userTermsConditionsRepository.existsByUserInfoId(userInfoId);
    }

    @Override
    public UserTermsConditionsEntity getUserTermsConditionsEntity(
            Long userInfoId, TermsConditionsType termsConditionsType) {
        UserTermsConditionsEntity userTermsConditions =
                userTermsConditionsRepository.findByUserInfoIdAndTermsConditionsType(
                        userInfoId, termsConditionsType);
        Optional.ofNullable(userTermsConditions)
                .orElseThrow(
                        () ->
                                new CustomNullPointException(
                                        termsConditionsType + ": 회원의 약관항목이 존재하지 않습니다"));
        return userTermsConditions;
    }
}
