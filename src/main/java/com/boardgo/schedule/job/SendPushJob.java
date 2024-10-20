package com.boardgo.schedule.job;

import com.boardgo.common.exception.FcmException;
import com.boardgo.domain.notification.service.NotificationCommandUseCase;
import com.boardgo.domain.notification.service.NotificationQueryUseCase;
import com.boardgo.domain.notification.service.response.NotificationPushResponse;
import com.boardgo.fcm.request.FcmMessageSendRequest;
import com.boardgo.fcm.service.FcmService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@DisallowConcurrentExecution
public class SendPushJob implements Job {

    @Autowired private NotificationQueryUseCase notificationQueryUseCase;
    @Autowired private NotificationCommandUseCase notificationCommandUseCase;
    @Autowired private FcmService fcmService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        List<NotificationPushResponse> notificationPushList =
                notificationQueryUseCase.getNotificationPushList();
        if (notificationPushList.isEmpty()) {
            return;
        }

        notificationPushList.forEach(
                push -> {
                    String fcmResult = "";
                    try {
                        fcmResult =
                                fcmService.sendFcmMessage(
                                        new FcmMessageSendRequest(
                                                push.token(),
                                                push.title(),
                                                push.content(),
                                                push.pathUrl()));
                        notificationCommandUseCase.saveNotificationResult(
                                push.notificationId(), fcmResult);
                    } catch (FcmException fe) {
                        // TODO fcm 푸시 실패 시 저장
                        log.info("send FCM FcmException :: {}", fe.getMessage());
                    } catch (NullPointerException npe) {
                        // TODO fcm 푸시 실패 시 저장
                        log.info("send FCM push NPE error :: {}", npe.getMessage());
                    }
                });
    }
}
