package com.boardgo.init;

import com.boardgo.domain.notification.entity.MessageType;
import com.boardgo.domain.notification.entity.NotificationMessageFormat;
import com.boardgo.domain.notification.repository.NotificationMessageFormatRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(4)
@Component
@Profile({"local", "test"})
@RequiredArgsConstructor
public class NotificationMessageFormatInitializer implements ApplicationRunner {
    private final NotificationMessageFormatRepository notificationMessageFormatRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<NotificationMessageFormat> notificationMessageFormatList = new ArrayList<>();
        for (MessageType messageType : MessageType.values()) {
            notificationMessageFormatList.add(
                    NotificationMessageFormat.builder()
                            .title("알림메세지 {#meetingTitle}")
                            .content("{#nickName} 님의 알림메세지 내용")
                            .messageType(messageType)
                            .build());
        }
        notificationMessageFormatRepository.saveAll(notificationMessageFormatList);
    }
}
