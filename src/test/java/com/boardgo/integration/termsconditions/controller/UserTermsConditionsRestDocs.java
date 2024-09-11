package com.boardgo.integration.termsconditions.controller;

import static com.boardgo.common.constant.HeaderConstant.API_VERSION_HEADER;
import static com.boardgo.common.constant.HeaderConstant.AUTHORIZATION;
import static com.boardgo.integration.fixture.TermsConditionsFixture.getTermsConditionsList;
import static io.restassured.RestAssured.given;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import com.boardgo.domain.termsconditions.controller.request.TermsConditionsCreateRequest;
import com.boardgo.domain.termsconditions.entity.enums.TermsConditionsType;
import com.boardgo.domain.termsconditions.repository.TermsConditionsRepository;
import com.boardgo.integration.support.RestDocsTestSupport;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;

public class UserTermsConditionsRestDocs extends RestDocsTestSupport {
    @Autowired private TermsConditionsRepository termsConditionsRepository;

    @BeforeEach
    void init() {
        termsConditionsRepository.saveAll(getTermsConditionsList());
    }

    @Test
    @DisplayName("회원의 약관동의를 저장한다")
    void 회원의_약관동의를_저장한다() {
        List<TermsConditionsCreateRequest> request = new ArrayList<>();
        for (TermsConditionsType type : TermsConditionsType.values()) {
            request.add(new TermsConditionsCreateRequest(type.name(), true));
        }

        given(this.spec)
                .port(port)
                .log()
                .all()
                .header(API_VERSION_HEADER, "1")
                .header(AUTHORIZATION, testAccessToken)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .filter(
                        document(
                                "post-terms-conditions-user",
                                getTermsConditionsUserRequestFieldsSnippet()))
                .body(request)
                .when()
                .post("/terms-conditions/user")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .log()
                .ifError()
                .extract();
    }

    private RequestFieldsSnippet getTermsConditionsUserRequestFieldsSnippet() {
        return requestFields(
                List.of(
                        fieldWithPath("[].termsConditionsType").type(STRING).description("약관동의 타입"),
                        fieldWithPath("[].agreement").type(BOOLEAN).description("동의 여부")));
    }
}
