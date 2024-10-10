package com.boardgo.fcm.service;

import com.google.firebase.internal.NonNull;
import com.google.firebase.messaging.Message;

public interface FirebaseMessagingUseCase {
    String send(@NonNull Message message);
}
