package com.boardgo.oauth2.dto;

import com.boardgo.domain.user.entity.ProviderType;

public record OAuth2CreateUserRequest(String email, ProviderType providerType) {}
