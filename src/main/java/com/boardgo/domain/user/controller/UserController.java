package com.boardgo.domain.user.controller;

import static com.boardgo.common.constant.HeaderConstant.API_VERSION_HEADER1;
import static com.boardgo.common.utils.SecurityUtils.currentUserId;

import com.boardgo.domain.user.controller.request.EmailRequest;
import com.boardgo.domain.user.controller.request.NickNameRequest;
import com.boardgo.domain.user.controller.request.PushTokenRequest;
import com.boardgo.domain.user.service.UserCommandUseCase;
import com.boardgo.domain.user.service.UserQueryUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserQueryUseCase userQueryUseCase;
    private final UserCommandUseCase userCommandUseCase;

    @GetMapping(value = "/check-email", headers = API_VERSION_HEADER1)
    public ResponseEntity<Void> checkEmail(@Valid EmailRequest emailRequest) {
        userQueryUseCase.existEmail(emailRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/check-nickname", headers = API_VERSION_HEADER1)
    public ResponseEntity<Void> checkNickName(@Valid NickNameRequest nickNameRequest) {
        userQueryUseCase.existNickName(nickNameRequest.nickName());
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/me", headers = API_VERSION_HEADER1)
    public ResponseEntity<String> me() {
        return ResponseEntity.ok(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @PatchMapping(value = "/push-token", headers = API_VERSION_HEADER1)
    public ResponseEntity<Void> updatePushToken(
            @RequestBody @Valid PushTokenRequest pushTokenRequest) {
        userCommandUseCase.updatePushToken(pushTokenRequest.pushToken(), currentUserId());
        return ResponseEntity.ok().build();
    }
}
