package com.boardgo.domain.user.controller;

import static com.boardgo.common.constant.HeaderConstant.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.boardgo.domain.user.controller.dto.SignupRequest;
import com.boardgo.domain.user.service.UserUseCase;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SignupControllerV1 {

	private final UserUseCase userUseCase;

	@PostMapping(value = "/signup", headers = API_VERSION_HEADER1, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> signup(@Valid @RequestBody SignupRequest signupRequest) {
		userUseCase.signup(signupRequest);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
}
