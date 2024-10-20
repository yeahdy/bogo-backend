package com.boardgo.fcm.request;

import jakarta.validation.constraints.NotBlank;

public record FcmMessageSendRequest(
        @NotBlank String token, @NotBlank String title, @NotBlank String content, String pathUrl) {}
