package com.boardgo.domain.user.controller;

import static com.boardgo.common.constant.HeaderConstant.API_VERSION_HEADER1;
import static com.boardgo.common.utils.SecurityUtils.currentUserId;

import com.boardgo.domain.user.controller.dto.SignupRequest;
import com.boardgo.domain.user.controller.dto.SocialSignupRequest;
import com.boardgo.domain.user.service.UserCommandUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SignupControllerV1 {

    private final UserCommandUseCase userCommandUseCase;

    @PostMapping(value = "/signup", headers = API_VERSION_HEADER1)
    public ResponseEntity<Void> signup(@Valid @RequestBody SignupRequest signupRequest) {
        userCommandUseCase.signup(signupRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping(value = "/social/signup", headers = API_VERSION_HEADER1)
    public ResponseEntity<Void> socialSignup(
            @Valid @RequestBody SocialSignupRequest socialSignupRequest) {
        userCommandUseCase.socialSignup(socialSignupRequest, currentUserId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
