package com.boardgo.domain.termsconditions.service;

import com.boardgo.domain.termsconditions.entity.UserTermsConditionsEntity;
import com.boardgo.domain.termsconditions.repository.UserTermsConditionsRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class UserTermsConditionsCommandServiceV1 implements UserTermsConditionsCommandUseCase {
    private final UserTermsConditionsRepository userTermsConditionsRepository;

    @Override
    public void createAll(List<UserTermsConditionsEntity> userTermsConditionsEntities) {
        userTermsConditionsRepository.saveAll(userTermsConditionsEntities);
    }
}
