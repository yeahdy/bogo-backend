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
        notificationMessageFormatList.add(create(MessageType.MEETING_MODIFY).build());
        notificationMessageFormatList.add(
                create(MessageType.MEETING_REMINDER)
                        .title("모임 날짜 하루 전이에요\uD83D\uDC4B")
                        .content("{#meetingTitle} 모임 하루 전이에요! 모임 날짜를 확인 해 주세요")
                        .build());
        notificationMessageFormatList.add(
                create(MessageType.REVIEW_RECEIVED)
                        .title("따끈따끈한 리뷰가 도착했어요\uD83D\uDCE8")
                        .content("{#meetingTitle} 모임을 함께한 참여자에게 리뷰를 받았어요! 리뷰를 확인해 보세요")
                        .build());
        notificationMessageFormatList.add(
                create(MessageType.REQUEST_REVIEW)
                        .title("{#nickName}님 오늘 모임 어떠셨나요?")
                        .content("오늘 함께한 참여자 분들에게 따듯한 리뷰를 남겨주세요\uD83D\uDC8C")
                        .build());
        notificationMessageFormatList.add(
                create(MessageType.KICKED_OUT)
                        .title("참여한 모임에서 퇴장되었어요\uD83D\uDE2D")
                        .content(
                                "아쉽게 {#meetingTitle} 모임에서 퇴장되었어요..\uD83D\uDCA6 {#nickName}님을 기다리는 다른 모임을 찾아보세요!")
                        .build());
        notificationMessageFormatRepository.saveAll(notificationMessageFormatList);
    }

    private NotificationMessageFormat.NotificationMessageFormatBuilder create(
            MessageType messageType) {
        return NotificationMessageFormat.builder()
                .title("모임의 정보가 변경되었어요!")
                .content("참여하신 {#meetingTitle} 모임의 정보가 변경되었어요. 지금 모임의 정보를 확인 해 보세요!")
                .messageType(messageType);
    }
}
