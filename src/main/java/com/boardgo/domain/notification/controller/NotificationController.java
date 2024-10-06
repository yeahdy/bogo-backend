package com.boardgo.domain.notification.controller;

import static com.boardgo.common.constant.HeaderConstant.API_VERSION_HEADER1;
import static com.boardgo.common.utils.SecurityUtils.currentUserId;

import com.boardgo.domain.notification.service.NotificationCommandUseCase;
import com.boardgo.domain.notification.service.NotificationQueryUseCase;
import com.boardgo.domain.notification.service.response.NotificationResponse;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
@Validated
public class NotificationController {

    private final NotificationQueryUseCase notificationQueryUseCase;
    private final NotificationCommandUseCase notificationCommandUseCase;

    @GetMapping(value = "/list", headers = API_VERSION_HEADER1)
    public ResponseEntity<List<NotificationResponse>> getNotificationList() {
        List<NotificationResponse> notifications =
                notificationQueryUseCase.getNotificationList(currentUserId());
        if (notifications.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(notifications);
    }

    @PatchMapping(value = "/read", headers = API_VERSION_HEADER1)
    public ResponseEntity<Void> readNotification(
            @RequestParam(name = "ids") @NotEmpty List<Long> notificationIds) {
        notificationCommandUseCase.readNotification(notificationIds);
        return ResponseEntity.ok().build();
    }
}
