package com.boardgo.integration.fixture;

import static com.boardgo.integration.data.TermsConditionsData.getTermsConditions;

import com.boardgo.domain.termsconditions.entity.TermsConditionsEntity;
import com.boardgo.domain.termsconditions.entity.enums.TermsConditionsType;
import java.util.ArrayList;
import java.util.List;

public abstract class TermsConditionsFixture {

    public static List<TermsConditionsEntity> getTermsConditionsList() {
        List<TermsConditionsEntity> termsConditionsList = new ArrayList<>();
        termsConditionsList.add(getTermsConditions(TermsConditionsType.TERMS).build());
        termsConditionsList.add(getTermsConditions(TermsConditionsType.PRIVACY).build());
        termsConditionsList.add(getTermsConditions(TermsConditionsType.LOCATION).build());
        termsConditionsList.add(getTermsConditions(TermsConditionsType.AGE14).build());
        termsConditionsList.add(
                getTermsConditions(TermsConditionsType.PUSH).required(false).build());
        return termsConditionsList;
    }
}
