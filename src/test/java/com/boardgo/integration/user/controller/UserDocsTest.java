package com.boardgo.integration.user.controller;

import static com.boardgo.common.constant.HeaderConstant.API_VERSION_HEADER;
import static com.boardgo.common.constant.HeaderConstant.AUTHORIZATION;
import static com.boardgo.integration.data.UserInfoData.userInfoEntityData;
import static io.restassured.RestAssured.given;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import com.boardgo.domain.user.controller.request.PushTokenRequest;
import com.boardgo.domain.user.entity.UserInfoStatus;
import com.boardgo.domain.user.entity.enums.ProviderType;
import com.boardgo.domain.user.repository.UserRepository;
import com.boardgo.integration.support.RestDocsTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;

public class UserDocsTest extends RestDocsTestSupport {

    @Autowired private UserRepository userRepository;

    @Test
    @DisplayName("사용자는 이메일을 중복 검사할 수 있다")
    void 사용자는_이메일을_중복_검사할_수_있다() {
        // given

        given(this.spec)
                .log()
                .all()
                .port(port)
                .header(API_VERSION_HEADER, "1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .queryParams("email", "aa@aa.com")
                .filter(
                        document(
                                "checkEmail",
                                queryParameters(
                                        parameterWithName("email")
                                                .description(
                                                        "이메일 주소 형식에 맞아야합니다! 아니면 BadRequest 발생"))))
                .when()
                .get("/check-email")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("사용자는 닉네임을 중복 검사할 수 있다")
    void 사용자는_닉네임을_중복_검사할_수_있다() {

        given(this.spec)
                .log()
                .all()
                .port(port)
                .header(API_VERSION_HEADER, "1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .queryParams("nickName", "nickname")
                .filter(
                        document(
                                "checkNickName",
                                queryParameters(
                                        parameterWithName("nickName")
                                                .description(
                                                        "공백이나 Null은 통과되지 않습니다! 아니면 BadRequest 발생"))))
                .when()
                .get("/check-nickname")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("회원 push token 갱신하기")
    void 회원_push_token_갱신하기() {
        userRepository.save(
                userInfoEntityData("3465227754", "FCM 입니다만")
                        .password(null)
                        .providerType(ProviderType.KAKAO)
                        .userInfoStatus(new UserInfoStatus())
                        .build());
        PushTokenRequest pushTokenRequest = new PushTokenRequest("ghdskjapushtokengskla");

        given(this.spec)
                .log()
                .all()
                .port(port)
                .header(API_VERSION_HEADER, "1")
                .header(AUTHORIZATION, testAccessToken)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .filter(document("patch-push-token", getPushTokenRequestFieldSnippet()))
                .body(pushTokenRequest)
                .when()
                .patch("/push-token")
                .then()
                .statusCode(HttpStatus.OK.value())
                .log()
                .ifError()
                .extract();
    }

    private RequestFieldsSnippet getPushTokenRequestFieldSnippet() {
        return requestFields(fieldWithPath("pushToken").type(STRING).description("FCM push token"));
    }
}
