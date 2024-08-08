package com.boardgo.oauth2.dto;

import static com.boardgo.domain.user.entity.ProviderType.KAKAO;

import java.util.Map;

public class KakaoResponse implements OAuth2Response {

    private final Map<String, Object> attribute;

    public KakaoResponse(Map<String, Object> attribute) {
        this.attribute = attribute;
    }

    @Override
    public String getProvider() {
        return KAKAO.toString();
    }

    @Override
    public String getProviderId() {
        return attribute.get("id").toString();
    }

    @Override
    public String getEmail() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attribute.get("kakao_account");
        if (kakaoAccount == null) {
            return null;
        }
        return kakaoAccount.get("email").toString();
    }

    @Override
    public String getName() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attribute.get("kakao_account");
        if (kakaoAccount == null) {
            return null;
        }
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        if (profile == null) {
            return null;
        }

        return profile.get("nickname").toString();
    }
}
