package com.boardgo.oauth2.dto;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.security.oauth2.client.provider")
public record OAuthProviderProperties(Provider google, Provider kakao) {
    public record Provider(String tokenUri, String userInfoUri, String userNameAttribute) {}
}
