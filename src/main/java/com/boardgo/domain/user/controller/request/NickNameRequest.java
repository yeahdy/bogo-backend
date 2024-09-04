package com.boardgo.domain.user.controller.request;

import jakarta.validation.constraints.NotEmpty;

public record NickNameRequest(@NotEmpty String nickName) {}
