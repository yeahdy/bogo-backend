package com.boardgo.domain.termsconditions.service;

import com.boardgo.domain.termsconditions.repository.UserTermsConditionsRepository;
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
}
