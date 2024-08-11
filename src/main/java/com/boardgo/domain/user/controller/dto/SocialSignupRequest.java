package com.boardgo.domain.user.controller.dto;

import com.boardgo.common.validator.annotation.ListInStringNotEmpty;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record SocialSignupRequest(
        @NotEmpty(message = "nickName") String nickName,
        @ListInStringNotEmpty(message = "prTags") List<String> prTags) {}
