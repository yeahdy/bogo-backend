package com.boardgo.integration.data;

import com.boardgo.domain.termsconditions.entity.TermsConditionsEntity;
import com.boardgo.domain.termsconditions.entity.enums.TermsConditionsType;

public abstract class TermsConditionsData {

    public static TermsConditionsEntity.TermsConditionsEntityBuilder getTermsConditions(
            TermsConditionsType type) {
        return TermsConditionsEntity.builder()
                .type(type)
                .title("약관동의 제목")
                .content("약관동의 내용")
                .required(true);
    }
}
