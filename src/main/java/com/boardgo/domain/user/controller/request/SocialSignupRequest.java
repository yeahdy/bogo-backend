package com.boardgo.domain.user.controller.request;

import com.boardgo.common.validator.annotation.ListInStringNotEmpty;
import com.boardgo.domain.termsconditions.controller.request.TermsConditionsCreateRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record SocialSignupRequest(
        @NotBlank(message = "nickName") String nickName,
        @ListInStringNotEmpty(message = "prTags") List<String> prTags,
        @NotEmpty List<TermsConditionsCreateRequest> termsConditions) {}
