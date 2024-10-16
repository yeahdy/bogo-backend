package com.boardgo.domain.notification.service;

import com.boardgo.domain.notification.entity.MessageType;
import com.boardgo.domain.notification.entity.NotificationMessageFormat;
import com.boardgo.domain.notification.repository.NotificationMessageFormatRepository;
import com.boardgo.domain.notification.service.request.NotificationCreateRequest;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationMessageFactory implements ApplicationRunner {

    private final NotificationMessageFormatRepository notificationMessageFormatRepository;
    private static final Map<MessageType, NotificationMessageFormat> MESSAGE_FORMAT =
            new ConcurrentHashMap<>();

    public static final String MEETING_TITLE = "{#meetingTitle}";
    public static final String NICKNAME = "{#nickName}";

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<NotificationMessageFormat> notificationMessageFormatList =
                notificationMessageFormatRepository.findAll();
        for (NotificationMessageFormat messageFormat : notificationMessageFormatList) {
            MESSAGE_FORMAT.put(messageFormat.getMessageType(), messageFormat);
        }
    }

    public static NotificationMessageFormat get(MessageType messageType) {
        return MESSAGE_FORMAT.get(messageType);
    }

    public static String replaceMessage(String message, NotificationCreateRequest param) {
        message = message.replace(MEETING_TITLE, param.meetingTitle());
        message = message.replace(NICKNAME, param.nickname());
        return message;
    }
}
