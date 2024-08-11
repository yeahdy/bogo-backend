package com.boardgo.domain.user.service;

import com.boardgo.domain.user.controller.dto.SignupRequest;
import com.boardgo.domain.user.controller.dto.SocialSignupRequest;

public interface UserCommandUseCase {
    Long signup(SignupRequest signupRequest);

    Long socialSignup(SocialSignupRequest socialSignupRequest, Long userId);
}
