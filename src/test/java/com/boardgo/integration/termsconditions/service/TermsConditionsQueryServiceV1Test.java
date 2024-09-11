package com.boardgo.integration.termsconditions.service;

import static com.boardgo.integration.fixture.TermsConditionsFixture.getTermsConditionsList;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.Assertions.assertThat;

import com.boardgo.domain.termsconditions.entity.TermsConditionsEntity;
import com.boardgo.domain.termsconditions.repository.TermsConditionsRepository;
import com.boardgo.domain.termsconditions.service.TermsConditionsQueryUseCase;
import com.boardgo.integration.support.IntegrationTestSupport;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TermsConditionsQueryServiceV1Test extends IntegrationTestSupport {
    @Autowired private TermsConditionsQueryUseCase termsConditionsQueryUseCase;
    @Autowired private TermsConditionsRepository termsConditionsRepository;

    @BeforeEach
    void init() {
        termsConditionsRepository.saveAll(getTermsConditionsList());
    }

    @Test
    @DisplayName("필수 동의 약관항목만 조회하기")
    void 필수_동의_약관항목만_조회하기() {
        // given
        List<Boolean> required = List.of(TRUE);
        // when
        List<TermsConditionsEntity> termsConditionsEntities =
                termsConditionsQueryUseCase.getTermsConditions(required);
        // then
        termsConditionsEntities.forEach(
                entity -> {
                    assertThat(entity.getRequired()).isTrue();
                });
    }

    @Test
    @DisplayName("선택 동의 약관항목만 조회하기")
    void 선택_동의_약관항목만_조회하기() {
        // given
        List<Boolean> required = List.of(FALSE);
        // when
        List<TermsConditionsEntity> termsConditionsEntities =
                termsConditionsQueryUseCase.getTermsConditions(required);
        // then
        termsConditionsEntities.forEach(
                entity -> {
                    assertThat(entity.getRequired()).isFalse();
                });
    }
}
