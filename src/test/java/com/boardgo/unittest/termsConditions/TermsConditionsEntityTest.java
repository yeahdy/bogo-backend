package com.boardgo.unittest.termsConditions;

import static com.boardgo.integration.data.TermsConditionsData.getTermsConditions;
import static com.boardgo.integration.fixture.TermsConditionsFixture.getTermsConditionsList;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.boardgo.domain.termsconditions.entity.TermsConditionsEntity;
import com.boardgo.domain.termsconditions.entity.enums.TermsConditionsType;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TermsConditionsEntityTest {

    @Test
    @DisplayName("필수 약관은 항상 동의되어야 한다")
    void 필수_약관은_항상_동의되어야_한다() {
        // given
        List<TermsConditionsEntity> termsConditionsEntities = getTermsConditionsList();

        // when
        // then
        for (TermsConditionsEntity termsConditions : termsConditionsEntities) {
            assertTrue(termsConditions.isRequired(true));
        }
    }

    @Test
    @DisplayName("약관항목 검사 시 필수 약관을 동의하지 않으면 false 를 반환한다")
    void 약관항목_검사_시_필수_약관을_동의하지_않으면_false_를_반환한다() {
        // given
        List<TermsConditionsEntity> termsConditionsEntities = getTermsConditionsList();

        // when
        // then
        for (TermsConditionsEntity termsConditions : termsConditionsEntities) {
            boolean isRequired = termsConditions.isRequired(false);
            if (TermsConditionsType.PUSH.equals(termsConditions.getType())) {
                continue;
            }
            assertFalse(isRequired);
        }
    }

    @Test
    @DisplayName("선택 약관동의 항목은 필수 약관항목 검사 시 제외이므로 동의유무에 상관없이 true 를 반환한다")
    void 선택_약관동의_항목은_필수_약관항목_검사_시_제외이므로_동의유무에_상관없이_true_를_반환한다() {
        // given
        List<TermsConditionsEntity> termsConditionsList = new ArrayList<>();
        termsConditionsList.add(getTermsConditions(TermsConditionsType.TERMS).build());
        termsConditionsList.add(getTermsConditions(TermsConditionsType.PRIVACY).build());
        termsConditionsList.add(getTermsConditions(TermsConditionsType.LOCATION).build());
        termsConditionsList.add(getTermsConditions(TermsConditionsType.AGE14).build());
        termsConditionsList.add(
                getTermsConditions(TermsConditionsType.PUSH).required(false).build());
        List<TermsConditionsEntity> termsConditionsEntities = getTermsConditionsList();

        // when
        // then
        for (TermsConditionsEntity termsConditions : termsConditionsEntities) {
            if (TermsConditionsType.PUSH.equals(termsConditions.getType())) {
                assertTrue(termsConditions.isRequired(false));
            }
            assertTrue(termsConditions.isRequired(true));
        }
    }
}
