package com.boardgo.integration.termsconditions.service;

import static com.boardgo.integration.fixture.TermsConditionsFixture.getTermsConditionsList;
import static org.assertj.core.api.Assertions.assertThat;

import com.boardgo.domain.termsconditions.entity.UserTermsConditionsEntity;
import com.boardgo.domain.termsconditions.entity.enums.TermsConditionsType;
import com.boardgo.domain.termsconditions.repository.TermsConditionsRepository;
import com.boardgo.domain.termsconditions.repository.UserTermsConditionsRepository;
import com.boardgo.domain.termsconditions.service.TermsConditionsFactory;
import com.boardgo.domain.termsconditions.service.UserTermsConditionsCommandUseCase;
import com.boardgo.integration.support.IntegrationTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

public class UserTermsConditionsCommandServiceV1Test extends IntegrationTestSupport {
    @Autowired private UserTermsConditionsCommandUseCase userTermsConditionsCommandUseCase;
    @Autowired private UserTermsConditionsRepository userTermsConditionsRepository;
    @Autowired private TermsConditionsRepository termsConditionsRepository;
    @Autowired private TermsConditionsFactory termsConditionsFactory;

    @BeforeEach
    void init() throws Exception {
        termsConditionsRepository.saveAll(getTermsConditionsList());
        termsConditionsFactory.run(null);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    @DisplayName("회원의 기존 푸시 약관동의가 Y라면 N으로 변경하고, N이라면 Y로 변경한다")
    void 회원의_기존_푸시_약관동의가_Y라면_N으로_변경하고_N이라면_Y로_변경한다(boolean original) {
        // given
        Long userId = 1L;
        userTermsConditionsRepository.save(
                UserTermsConditionsEntity.builder()
                        .userInfoId(userId)
                        .termsConditionsEntity(
                                TermsConditionsFactory.get(TermsConditionsType.PUSH.toString()))
                        .agreement(original)
                        .build());

        // when
        userTermsConditionsCommandUseCase.updatePushTermsCondition(userId);

        // then
        UserTermsConditionsEntity push =
                userTermsConditionsRepository.findByUserInfoId(userId).getFirst();
        assertThat(push.getTermsConditions().getType()).isEqualTo(TermsConditionsType.PUSH);
        assertThat(push.getAgreement()).isEqualTo(!original);
    }
}
