package com.boardgo.integration.termsconditions.controller;

import static com.boardgo.common.constant.HeaderConstant.API_VERSION_HEADER;
import static com.boardgo.common.constant.HeaderConstant.AUTHORIZATION;
import static com.boardgo.integration.data.TermsConditionsData.getTermsConditions;
import static com.boardgo.integration.data.UserInfoData.userInfoEntityData;
import static io.restassured.RestAssured.given;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import com.boardgo.domain.termsconditions.entity.TermsConditionsEntity;
import com.boardgo.domain.termsconditions.entity.UserTermsConditionsEntity;
import com.boardgo.domain.termsconditions.entity.enums.TermsConditionsType;
import com.boardgo.domain.termsconditions.repository.TermsConditionsRepository;
import com.boardgo.domain.termsconditions.repository.UserTermsConditionsRepository;
import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.domain.user.repository.UserRepository;
import com.boardgo.integration.support.RestDocsTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.request.QueryParametersSnippet;

public class UserTermsConditionsRestDocs extends RestDocsTestSupport {

    @Autowired UserRepository userRepository;
    @Autowired private TermsConditionsRepository termsConditionsRepository;
    @Autowired private UserTermsConditionsRepository userTermsConditionsRepository;

    @BeforeEach
    void init() {
        UserInfoEntity user = userInfoEntityData("user1@naver.com", "user1").build();
        userRepository.save(user);
    }

    @Test
    @DisplayName("회원의 PUSH 약관동의를 수정한다")
    void 회원의_PUSH_약관동의를_수정한다() {
        // given
        // 회원 푸시약관동의 활성화
        TermsConditionsEntity termsConditionsEntity =
                getTermsConditions(TermsConditionsType.PUSH).required(false).build();
        termsConditionsRepository.save(termsConditionsEntity);
        userTermsConditionsRepository.save(
                UserTermsConditionsEntity.builder()
                        .userInfoId(1L)
                        .termsConditionsEntity(termsConditionsEntity)
                        .agreement(Boolean.FALSE)
                        .build());

        given(this.spec)
                .log()
                .all()
                .port(port)
                .header(API_VERSION_HEADER, "1")
                .header(AUTHORIZATION, testAccessToken)
                .queryParam("isAgreed", Boolean.TRUE)
                .filter(
                        document(
                                "patch-user-terms-conditions-push",
                                getUserTermsConditionsQueryParamSnippet()))
                .when()
                .patch("/user-terms-conditions/push")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    private QueryParametersSnippet getUserTermsConditionsQueryParamSnippet() {
        return queryParameters(parameterWithName("isAgreed").description("동의여부(true/false)"));
    }
}
