package com.boardgo.domain.user.service;

import com.boardgo.domain.user.controller.dto.SignupRequest;

public interface UserUseCase {
	Long signup(SignupRequest signupRequest);
}
