package com.boardgo.integration.oauth2;

import static org.assertj.core.api.Assertions.assertThat;

import com.boardgo.integration.support.IntegrationTestSupport;
import com.boardgo.oauth2.dto.OAuthProviderProperties;
import com.boardgo.oauth2.dto.OAuthRegistrationProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class OAuthPropertiesTest extends IntegrationTestSupport {

    @Autowired private OAuthProviderProperties providerProperties;
    @Autowired private OAuthRegistrationProperties registrationProperties;

    @Test
    @DisplayName("kakao provide properties 매핑")
    void kakao_provide_properties_매핑() {
        // given
        String TOKEN_REQUEST_URI = providerProperties.kakao().tokenUri();
        String USER_INFO_REQUEST_URI = providerProperties.kakao().userInfoUri();

        // then
        assertThat(TOKEN_REQUEST_URI).isEqualTo("https://kauth.kakao.com/oauth/token");
        assertThat(USER_INFO_REQUEST_URI).isEqualTo("https://kapi.kakao.com/v2/user/me");
    }

    @Test
    @DisplayName("redirect uri properties 매핑")
    void redirect_uri_properties_매핑() {
        // given
        String GOOGLE_REDIRECT_URI = registrationProperties.google().redirectUri();
        String KAKAO_REDIRECT_URI = registrationProperties.kakao().redirectUri();

        // then
        assertThat(GOOGLE_REDIRECT_URI).contains("/login/oauth2/code/google");
        assertThat(KAKAO_REDIRECT_URI).contains("/login/oauth2/code/kakao");
    }
}
