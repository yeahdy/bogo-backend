package com.boardgo.integration.user.controller;

import static com.boardgo.common.constant.HeaderConstant.API_VERSION_HEADER;
import static io.restassured.RestAssured.given;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import com.boardgo.domain.user.controller.request.SignupRequest;
import com.boardgo.integration.support.RestDocsTestSupport;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class SignupDocsTest extends RestDocsTestSupport {

    @Test
    @DisplayName("사용자는 회원가입을 진행할 수 있다V1")
    void 사용자는_회원가입을_진행할_수_있다V1() {
        // given
        SignupRequest signupRequest =
                new SignupRequest("aa@aa.aa", "nickname", "password", List.of("prTag1", "prTag2"));

        given(this.spec)
                .log()
                .all()
                .port(port)
                .header(API_VERSION_HEADER, "1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(signupRequest)
                .filter(
                        document(
                                "signup",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("email")
                                                .type(STRING)
                                                .description("이메일 주소 형식에 맞아야합니다!"),
                                        fieldWithPath("nickName").type(STRING).description("닉네임"),
                                        fieldWithPath("password")
                                                .type(STRING)
                                                .description("8자 이상 50자 이하"),
                                        fieldWithPath("prTags")
                                                .type(ARRAY)
                                                .description("회원가입 시 입력한 태그들 (각 요소는 빈 문자열일 수 없습니다)")
                                                .optional(),
                                        fieldWithPath("providerType").ignored())))
                .when()
                .post("/signup")
                .then()
                .statusCode(HttpStatus.CREATED.value());
    }
}
