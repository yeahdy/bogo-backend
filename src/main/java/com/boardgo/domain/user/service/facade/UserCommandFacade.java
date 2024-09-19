package com.boardgo.domain.user.service.facade;

import com.boardgo.domain.user.controller.request.SignupRequest;
import com.boardgo.domain.user.controller.request.SocialSignupRequest;

public interface UserCommandFacade {
    Long signup(SignupRequest signupRequest);

    Long socialSignup(SocialSignupRequest signupRequest, Long userId);
}
