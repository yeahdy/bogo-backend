package com.boardgo.integration.user.controller;

import static com.boardgo.common.constant.HeaderConstant.API_VERSION_HEADER;
import static com.boardgo.common.constant.HeaderConstant.AUTHORIZATION;
import static com.boardgo.integration.data.UserInfoData.userInfoEntityData;
import static com.boardgo.integration.fixture.TermsConditionsFixture.getTermsConditionsList;
import static io.restassured.RestAssured.given;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import com.boardgo.config.UserNotificationSettingCommandUseCaseTestConfig;
import com.boardgo.domain.termsconditions.controller.request.TermsConditionsCreateRequest;
import com.boardgo.domain.termsconditions.entity.enums.TermsConditionsType;
import com.boardgo.domain.termsconditions.repository.TermsConditionsRepository;
import com.boardgo.domain.termsconditions.service.TermsConditionsFactory;
import com.boardgo.domain.user.controller.request.SignupRequest;
import com.boardgo.domain.user.controller.request.SocialSignupRequest;
import com.boardgo.domain.user.entity.enums.ProviderType;
import com.boardgo.domain.user.repository.UserRepository;
import com.boardgo.integration.support.RestDocsTestSupport;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;

@Import({UserNotificationSettingCommandUseCaseTestConfig.class})
public class SignupDocsTest extends RestDocsTestSupport {

    @Autowired private TermsConditionsFactory termsConditionsFactory;
    @Autowired private TermsConditionsRepository termsConditionsRepository;
    @Autowired private UserRepository userRepository;

    @BeforeEach
    void init() throws Exception {
        termsConditionsRepository.saveAll(getTermsConditionsList());
        termsConditionsFactory.run(null);
    }

    @Test
    @DisplayName("사용자는 회원가입을 진행할 수 있다V1")
    void 사용자는_회원가입을_진행할_수_있다V1() {
        // given
        List<TermsConditionsCreateRequest> termsConditions = new ArrayList<>();
        for (TermsConditionsType type : TermsConditionsType.values()) {
            termsConditions.add(new TermsConditionsCreateRequest(type.name(), true));
        }
        SignupRequest signupRequest =
                new SignupRequest(
                        "aa@aa.aa",
                        "nickname",
                        "password",
                        List.of("prTag1", "prTag2"),
                        termsConditions);

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
                                        fieldWithPath("providerType").ignored(),
                                        fieldWithPath("termsConditions")
                                                .type(JsonFieldType.ARRAY)
                                                .description("약관동의 목록"),
                                        fieldWithPath("termsConditions[].termsConditionsType")
                                                .type(STRING)
                                                .description("약관동의 타입"),
                                        fieldWithPath("termsConditions[].agreement")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("약관동의 동의 여부"))))
                .when()
                .post("/signup")
                .then()
                .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("소셜로그인 시 서비스 회원가입을 한다")
    void 소셜로그인_시_서비스_회원가입을_한다() {
        List<TermsConditionsCreateRequest> termsConditions = new ArrayList<>();
        for (TermsConditionsType type : TermsConditionsType.values()) {
            termsConditions.add(new TermsConditionsCreateRequest(type.name(), true));
        }
        userRepository.save(
                userInfoEntityData("abc123@google.com", "googoo")
                        .providerType(ProviderType.GOOGLE)
                        .password(null)
                        .build());

        SocialSignupRequest socialSignupRequest =
                new SocialSignupRequest("Bread", List.of("ENFJ", "HAPPY"), termsConditions);

        given(this.spec)
                .port(port)
                .log()
                .all()
                .header(API_VERSION_HEADER, "1")
                .header(AUTHORIZATION, testAccessToken)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .filter(document("social-signup", getSocialSignupRequestFieldsSnippet()))
                .body(socialSignupRequest)
                .urlEncodingEnabled(false)
                .when()
                .post("/social/signup")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .log()
                .ifError()
                .extract();
    }

    private RequestFieldsSnippet getSocialSignupRequestFieldsSnippet() {
        return requestFields(
                fieldWithPath("nickName").type(STRING).description("닉네임"),
                fieldWithPath("prTags").type(ARRAY).description("PR태그").optional(),
                fieldWithPath("termsConditions").type(JsonFieldType.ARRAY).description("약관동의 목록"),
                fieldWithPath("termsConditions[].termsConditionsType")
                        .type(STRING)
                        .description("약관동의 타입"),
                fieldWithPath("termsConditions[].agreement")
                        .type(JsonFieldType.BOOLEAN)
                        .description("약관동의 동의 여부"));
    }
}
