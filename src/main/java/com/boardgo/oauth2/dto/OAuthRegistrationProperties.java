package com.boardgo.oauth2.dto;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.security.oauth2.client.registration")
public record OAuthRegistrationProperties(Registration google, Registration kakao) {

    public record Registration(String clientId, String clientSecret, String redirectUri) {}
}
