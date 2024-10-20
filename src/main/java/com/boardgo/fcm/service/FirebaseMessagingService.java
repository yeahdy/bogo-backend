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
            String response = firebaseMessaging.send(message);
            log.info("Sent FCM response :: " + response);
            return response;
        } catch (FirebaseMessagingException e) {
            throw new FcmException(e.getMessage());
        }
    }
}
