package com.boardgo.fcm.service;

import com.boardgo.common.exception.FcmException;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Profile("!test")
@RequiredArgsConstructor
public class FirebaseMessagingService implements FirebaseMessagingUseCase {

    private final FirebaseMessaging firebaseMessaging;

    @Override
    public String send(Message message) {
        try {
            log.info("Send FCM Message :: " + message.toString());
            String response = firebaseMessaging.send(message);
            log.info("Sent FCM response :: " + response);
            return response; // TODO FCM 발송 성공 결과 저장
        } catch (FirebaseMessagingException e) {
            // TODO FCM 발송 실패 예외처리
            throw new FcmException(e.getMessage());
        }
    }
}
