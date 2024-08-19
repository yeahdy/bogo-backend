package com.boardgo.oauth2.dto;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oauth-login.redirect")
public record OAuthLoginProperties(String domain, String main, String signup) {}
