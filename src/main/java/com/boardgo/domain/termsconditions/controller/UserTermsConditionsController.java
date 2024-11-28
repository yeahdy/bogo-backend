package com.boardgo.domain.termsconditions.controller;

import static com.boardgo.common.constant.HeaderConstant.API_VERSION_HEADER1;
import static com.boardgo.common.utils.SecurityUtils.currentUserId;

import com.boardgo.domain.termsconditions.service.facade.UserTermsConditionsCommandFacade;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-terms-conditions")
@Validated
public class UserTermsConditionsController {
    private final UserTermsConditionsCommandFacade userTermsConditionsCommandFacade;

    @PatchMapping(value = "/push", headers = API_VERSION_HEADER1)
    public ResponseEntity<Void> updateUserNotificationSetting(
            @RequestParam(name = "isAgreed") @NotNull Boolean isAgreed) {
        userTermsConditionsCommandFacade.updatePushTermsConditions(currentUserId(), isAgreed);
        return ResponseEntity.ok().build();
    }
}
