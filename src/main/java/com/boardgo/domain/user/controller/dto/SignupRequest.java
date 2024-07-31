package com.boardgo.domain.user.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

public record SignupRequest(
	@Email(message = "email") String email,
	@NotEmpty(message = "nickname") String nickName,
	@Min(value = 8, message = "password") @Max(value = 50, message = "password") String password
) {
}
