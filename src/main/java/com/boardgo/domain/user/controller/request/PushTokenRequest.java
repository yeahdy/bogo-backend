package com.boardgo.domain.user.controller.request;

import jakarta.validation.constraints.NotBlank;

public record PushTokenRequest(@NotBlank String pushToken) {}
