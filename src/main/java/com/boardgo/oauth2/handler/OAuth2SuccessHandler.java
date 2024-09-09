package com.boardgo.oauth2.handler;

import static com.boardgo.common.constant.HeaderConstant.*;
import static com.boardgo.common.constant.TimeConstant.*;
import static com.boardgo.common.utils.CookieUtils.*;
import static com.boardgo.common.utils.CustomStringUtils.*;

import com.boardgo.config.log.OutputLog;
import com.boardgo.jwt.service.LoginService;
import com.boardgo.jwt.service.TokenService;
import com.boardgo.oauth2.dto.OAuthLoginProperties;
import com.boardgo.oauth2.entity.CustomOAuth2User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final OAuthLoginProperties properties;
    private final TokenService tokenService;
    private final LoginService loginService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        String accessToken =
                tokenService.getAccessToken(oAuth2User.getId(), oAuth2User.getRoleType());
        ResponseCookie responseCookie =
                createCookies(
                        AUTHORIZATION, accessToken, properties.domain(), ACCESS_TOKEN_DURATION);
        response.setHeader("Set-cookie", responseCookie.toString());
        // String refreshToken = tokenService.getRefreshToken(oAuth2User.getId(),
        // oAuth2User.getRoleType(),
        //     properties.domain());
        // response.setHeader("Set-cookie", refreshToken);
        // response.setHeader("Set-cookie", refreshToken);
        //
        // loginService.create(refreshToken, oAuth2User.getId());

        String redirectUrl = properties.main();
        if (!existString(oAuth2User.getNickname())) {
            redirectUrl =
                    UriComponentsBuilder.fromUriString(properties.signup())
                            .queryParam("type", "social")
                            .build()
                            .toUriString();
        }
        OutputLog.logInfo("OAuth2 redirectUrl :: " + redirectUrl);
        OutputLog.logInfo(oAuth2User.getAttributes().toString());
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
