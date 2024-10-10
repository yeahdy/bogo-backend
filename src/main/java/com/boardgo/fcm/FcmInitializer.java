package com.boardgo.fcm;

import com.boardgo.common.exception.FcmException;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Slf4j
@Profile("!test")
@Component
public class FcmInitializer {
    @Value("${firebase.key-path}")
    private String KEY_PATH;

    @PostConstruct
    public void initializeFirebase() {
        try {
            InputStream refreshToken = new ClassPathResource(KEY_PATH).getInputStream();

            FirebaseOptions options =
                    FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.fromStream(refreshToken))
                            .build();

            FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            throw new FcmException(e.getMessage());
        }
    }

    @Bean
    public FirebaseMessaging firebaseMessaging() {
        return FirebaseMessaging.getInstance();
    }
}
