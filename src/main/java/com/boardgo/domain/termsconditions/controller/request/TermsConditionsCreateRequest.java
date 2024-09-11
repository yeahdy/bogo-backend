package com.boardgo.domain.termsconditions.controller.request;

import com.boardgo.common.validator.annotation.EnumValue;
import com.boardgo.domain.termsconditions.entity.enums.TermsConditionsType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TermsConditionsCreateRequest(
        @NotBlank @EnumValue(enumClass = TermsConditionsType.class, message = "유효하지 않은 약관동의 타입입니다")
                String termsConditionsType,
        @NotNull Boolean agreement) {}
