package com.boardgo.integration.user.controller;

import static com.boardgo.common.constant.HeaderConstant.*;
import static io.restassured.RestAssured.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.*;

import com.boardgo.integration.support.RestDocsTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class UserDocsTest extends RestDocsTestSupport {

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
}
