package com.boardgo.domain.user.controller.dto;

import jakarta.validation.constraints.NotEmpty;

public record NickNameRequest(@NotEmpty String nickName) {}
