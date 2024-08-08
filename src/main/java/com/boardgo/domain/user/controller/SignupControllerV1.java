package com.boardgo.domain.user.controller;

import static com.boardgo.common.constant.HeaderConstant.API_VERSION_HEADER1;

import com.boardgo.domain.user.controller.dto.SignupRequest;
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
    public ResponseEntity<Void> socialSignup() {
        // TODO. 소셜회원가입 닉네임, PR 태그 설정
        //       userUseCase.socialSignup();
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
