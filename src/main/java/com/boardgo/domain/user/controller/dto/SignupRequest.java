package com.boardgo.domain.user.controller.dto;

import com.boardgo.common.validator.annotation.ListInStringNotEmpty;
import com.boardgo.domain.user.entity.enums.ProviderType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public record SignupRequest(
        @Email(message = "email") String email,
        @NotEmpty(message = "nickName") String nickName,
        @Size(min = 8, max = 50, message = "message") String password,
        @ListInStringNotEmpty(message = "prTags") List<String> prTags,
        ProviderType providerType) {
    public SignupRequest(String email, String nickName, String password, List<String> prTags) {
        this(email, nickName, password, prTags, ProviderType.LOCAL);
    }
}
