package com.boardgo.domain.user.controller.request;

import com.boardgo.common.validator.annotation.ListInStringNotEmpty;
import com.boardgo.domain.termsconditions.controller.request.TermsConditionsCreateRequest;
import com.boardgo.domain.user.entity.enums.ProviderType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public record SignupRequest(
        @Email(message = "email") String email,
        @NotBlank(message = "nickName") String nickName,
        @Size(min = 8, max = 50, message = "password") String password,
        @ListInStringNotEmpty(message = "prTags") List<String> prTags,
        ProviderType providerType,
        List<TermsConditionsCreateRequest> termsConditions) {
    public SignupRequest(
            String email,
            String nickName,
            String password,
            List<String> prTags,
            List<TermsConditionsCreateRequest> termsConditions) {
        this(email, nickName, password, prTags, ProviderType.LOCAL, termsConditions);
    }
}
