package com.boardgo.oauth2.dto;

import com.boardgo.common.exception.OAuth2Exception;
import com.boardgo.domain.user.entity.enums.ProviderType;
import java.util.Map;

public class OAuth2UserResponseFactory {
    public static OAuth2Response getOAuth2Response(
            ProviderType providerType, Map<String, Object> attribute) {
        switch (providerType) {
            case GOOGLE:
                return new GoogleResponse(attribute);
            case KAKAO:
                return new KakaoResponse(attribute);
            default:
                throw new OAuth2Exception("Invalid Provider Type");
        }
    }
}
