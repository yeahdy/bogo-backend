package com.boardgo.oauth2.handler;

import static com.boardgo.common.constant.HeaderConstant.AUTHORIZATION;
import static com.boardgo.common.constant.TimeConstant.ACCESS_TOKEN_DURATION;
import static com.boardgo.common.utils.CookieUtils.createCookies;
import static com.boardgo.common.utils.CustomStringUtils.existString;

import com.boardgo.jwt.JWTUtil;
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
    private final JWTUtil jwtUtil;
    private final OAuthLoginProperties properties;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        String accessToken =
                jwtUtil.createJwt(
                        oAuth2User.getId(), oAuth2User.getRoleType(), ACCESS_TOKEN_DURATION);
        //        Cookie cookies = createCookies(AUTHORIZATION, accessToken);
        //        cookies.setDomain(properties.domain());
        //        response.addCookie(cookies);

        ResponseCookie responseCookie =
                createCookies(AUTHORIZATION, accessToken, properties.domain());
        response.setHeader("Set-cookie", responseCookie.toString());

        String redirectUrl = properties.main();
        if (!existString(oAuth2User.getNickname())) {
            redirectUrl =
                    UriComponentsBuilder.fromUriString(properties.signup())
                            .queryParam("type", "social")
                            .build()
                            .toUriString();
        }

        log.info("OAuth2 :: {} redirectUrl :: {} ", oAuth2User.getAttributes(), redirectUrl);
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
