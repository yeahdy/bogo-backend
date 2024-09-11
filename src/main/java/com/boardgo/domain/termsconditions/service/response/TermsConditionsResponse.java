package com.boardgo.domain.termsconditions.service.response;

import com.boardgo.domain.termsconditions.entity.enums.TermsConditionsType;

public record TermsConditionsResponse(
        TermsConditionsType type, String title, String content, Boolean required) {}
