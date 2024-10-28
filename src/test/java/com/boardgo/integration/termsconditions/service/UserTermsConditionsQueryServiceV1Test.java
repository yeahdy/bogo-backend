package com.boardgo.integration.termsconditions.service;

import static com.boardgo.integration.fixture.TermsConditionsFixture.getTermsConditionsList;
import static org.assertj.core.api.Assertions.assertThat;

import com.boardgo.domain.termsconditions.entity.UserTermsConditionsEntity;
import com.boardgo.domain.termsconditions.entity.enums.TermsConditionsType;
import com.boardgo.domain.termsconditions.repository.TermsConditionsRepository;
import com.boardgo.domain.termsconditions.repository.UserTermsConditionsRepository;
import com.boardgo.domain.termsconditions.service.TermsConditionsFactory;
import com.boardgo.domain.termsconditions.service.UserTermsConditionsQueryUseCase;
import com.boardgo.integration.support.IntegrationTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;

public class UserTermsConditionsQueryServiceV1Test extends IntegrationTestSupport {

    @Autowired private UserTermsConditionsQueryUseCase userTermsConditionsQueryUseCase;
    @Autowired private TermsConditionsRepository termsConditionsRepository;
    @Autowired private UserTermsConditionsRepository userTermsConditionsRepository;
    @Autowired private TermsConditionsFactory termsConditionsFactory;

    @BeforeEach
    void init() {
        termsConditionsRepository.saveAll(getTermsConditionsList());
    }

    @ParameterizedTest
    @DisplayName("약관동의 별로 회원의 약관동의 유무를 확인할 수 있다")
    @EnumSource(TermsConditionsType.class)
    void 약관동의_별로_회원의_약관동의_유무를_확인할_수_있다(TermsConditionsType termsConditionsType) throws Exception {
        // given
        termsConditionsFactory.run(null);
        Long userId = 1L;
        boolean agreement = true;
        for (TermsConditionsType type : TermsConditionsType.values()) {
            userTermsConditionsRepository.save(
                    UserTermsConditionsEntity.builder()
                            .userInfoId(userId)
                            .termsConditionsEntity(TermsConditionsFactory.get(type.toString()))
                            .agreement(agreement)
                            .build());
        }

        // when
        UserTermsConditionsEntity userTermsConditionsEntity =
                userTermsConditionsQueryUseCase.getUserTermsConditionsEntity(
                        userId, termsConditionsType);

        // then
        assertThat(userTermsConditionsEntity.getTermsConditions().getType())
                .isEqualTo(termsConditionsType);
        assertThat(userTermsConditionsEntity.getUserInfoId()).isEqualTo(userId);
        assertThat(userTermsConditionsEntity.getAgreement()).isEqualTo(agreement);
    }
}
