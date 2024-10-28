package com.boardgo.domain.notification.controller;

import static com.boardgo.common.constant.HeaderConstant.API_VERSION_HEADER1;
import static com.boardgo.common.utils.SecurityUtils.currentUserId;

import com.boardgo.domain.notification.service.UserNotificationSettingQueryUseCase;
import com.boardgo.domain.notification.service.response.UserNotificationSettingResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-notification")
public class UserNotificationSettingController {

    private final UserNotificationSettingQueryUseCase userNotificationSettingQueryUseCase;

    @GetMapping(value = "/list", headers = API_VERSION_HEADER1)
    public ResponseEntity<List<UserNotificationSettingResponse>> getUserNotificationSettingList() {
        List<UserNotificationSettingResponse> userNotificationSettingsList =
                userNotificationSettingQueryUseCase.getUserNotificationSettingsList(
                        currentUserId());
        return ResponseEntity.ok(userNotificationSettingsList);
    }
}
