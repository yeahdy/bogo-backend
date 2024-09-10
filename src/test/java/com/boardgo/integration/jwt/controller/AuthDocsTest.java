package com.boardgo.integration.jwt.controller;

import static com.boardgo.common.constant.HeaderConstant.*;
import static io.restassured.RestAssured.*;
import static org.springframework.restdocs.cookies.CookieDocumentation.*;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.*;

import com.boardgo.common.constant.TimeConstant;
import com.boardgo.common.utils.CookieUtils;
import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.domain.user.entity.enums.RoleType;
import com.boardgo.domain.user.repository.UserRepository;
import com.boardgo.integration.data.UserInfoData;
import com.boardgo.integration.support.RestDocsTestSupport;
import com.boardgo.jwt.entity.AuthEntity;
import com.boardgo.jwt.repository.AuthRepository;
import com.boardgo.jwt.service.TokenService;
import jakarta.servlet.http.Cookie;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.cookies.RequestCookiesSnippet;

public class AuthDocsTest extends RestDocsTestSupport {
    @Autowired private TokenService tokenService;
    @Autowired private UserRepository userRepository;
    @Autowired private AuthRepository authRepository;

    @Test
    @DisplayName("사용자는 리프레시 토큰과 액세스 토큰을 재발급 할 수 있다")
    void 사용자는_리프레시_토큰과_액세스_토큰을_재발급_할_수_있다() {
        // given
        UserInfoEntity userInfo = UserInfoData.userInfoEntityData("aa@aa.com", "nickname").build();
        UserInfoEntity savedUser = userRepository.save(userInfo);
        String refreshToken = tokenService.getRefreshToken(savedUser.getId(), RoleType.USER);
        AuthEntity auth =
                AuthEntity.builder()
                        .userInfo(userInfo)
                        .refreshToken(refreshToken)
                        .expirationDatetime(LocalDateTime.now().plusDays(1))
                        .build();
        AuthEntity savedAuth = authRepository.save(auth);

        Cookie cookie =
                CookieUtils.createCookies(
                        AUTHORIZATION, refreshToken, TimeConstant.REFRESH_TOKEN_DURATION);
        // when
        // then
        given(this.spec)
                .log()
                .all()
                .port(port)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(API_VERSION_HEADER, "1")
                .cookie(AUTHORIZATION, cookie.getValue())
                .filter(document("reissue", getRequestCookiesSnippet()))
                .when()
                .post("/reissue")
                .then()
                .log()
                .ifError()
                .statusCode(HttpStatus.OK.value());
    }

    private RequestCookiesSnippet getRequestCookiesSnippet() {
        return requestCookies(cookieWithName(AUTHORIZATION).description("Refresh Token"));
    }
}
