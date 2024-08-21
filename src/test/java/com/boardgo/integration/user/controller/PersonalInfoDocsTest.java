package com.boardgo.integration.user.controller;

import static com.boardgo.common.constant.HeaderConstant.API_VERSION_HEADER;
import static com.boardgo.common.constant.HeaderConstant.AUTHORIZATION;
import static com.boardgo.integration.fixture.UserInfoFixture.localUserInfoEntity;
import static com.boardgo.integration.fixture.UserInfoFixture.socialUserInfoEntity;
import static com.boardgo.integration.fixture.UserPrTagFixture.userPrTagEntity;
import static io.restassured.RestAssured.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import com.boardgo.domain.user.controller.dto.UserPersonalInfoUpdateRequest;
import com.boardgo.domain.user.entity.ProviderType;
import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.domain.user.repository.UserPrTagRepository;
import com.boardgo.domain.user.repository.UserRepository;
import com.boardgo.integration.support.RestDocsTestSupport;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.request.PathParametersSnippet;
import org.springframework.restdocs.request.RequestPartsSnippet;

public class PersonalInfoDocsTest extends RestDocsTestSupport {

    @Autowired private UserRepository userRepository;
    @Autowired private UserPrTagRepository userPrTagRepository;

    @Test
    @DisplayName("내 개인정보 조회하기")
    void 내_개인정보_조회하기() {
        UserInfoEntity userInfo = userRepository.save(socialUserInfoEntity(ProviderType.KAKAO));
        userPrTagRepository.save(userPrTagEntity(userInfo.getId(), "가나다"));
        userPrTagRepository.save(userPrTagEntity(userInfo.getId(), "456"));

        given(this.spec)
                .log()
                .all()
                .port(port)
                .header(API_VERSION_HEADER, "1")
                .header(AUTHORIZATION, testAccessToken)
                .filter(document("get-personal-info", getPersonalInfoResponseFieldsSnippet()))
                .when()
                .get("/personal-info")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    private ResponseFieldsSnippet getPersonalInfoResponseFieldsSnippet() {
        return responseFields(
                fieldWithPath("email").type(JsonFieldType.STRING).description("회원 고유 ID"),
                fieldWithPath("nickName").type(JsonFieldType.STRING).description("닉네임"),
                fieldWithPath("profileImage").type(JsonFieldType.STRING).description("프로필 이미지"),
                fieldWithPath("averageRating").type(JsonFieldType.NUMBER).description("평균 별점"),
                fieldWithPath("prTags")
                        .type(JsonFieldType.ARRAY)
                        .description("PR태그 (없을 경우 빈배열 반환)"));
    }

    @Test
    @DisplayName("내 개인정보 수정하기")
    void 내_개인정보_수정하기() throws JsonProcessingException {
        UserPersonalInfoUpdateRequest updateRequest =
                new UserPersonalInfoUpdateRequest("butter", "fskdj234#!@");
        String jsonValue = objectMapper.writeValueAsString(updateRequest);
        userRepository.save(localUserInfoEntity());

        given(this.spec)
                .log()
                .all()
                .port(port)
                .header(API_VERSION_HEADER, "1")
                .header(AUTHORIZATION, testAccessToken)
                .contentType(ContentType.JSON)
                .filter(
                        document(
                                "patch-personal-info", getPersonalInfoUpdateRequestFieldsSnippet()))
                .body(jsonValue)
                .when()
                .patch("/personal-info")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    private RequestFieldsSnippet getPersonalInfoUpdateRequestFieldsSnippet() {
        return requestFields(
                fieldWithPath("nickName").type(JsonFieldType.STRING).description("닉네임").optional(),
                fieldWithPath("password")
                        .type(JsonFieldType.STRING)
                        .description("비밀번호")
                        .optional());
    }

    @Test
    @DisplayName("내 PR태그 수정하기")
    void 내_PR태그_수정하기() {
        UserInfoEntity userInfo = userRepository.save(socialUserInfoEntity(ProviderType.KAKAO));
        userPrTagRepository.save(userPrTagEntity(userInfo.getId(), "123"));
        userPrTagRepository.save(userPrTagEntity(userInfo.getId(), "456"));
        userPrTagRepository.save(userPrTagEntity(userInfo.getId(), "789"));

        given(this.spec)
                .log()
                .all()
                .port(port)
                .header(API_VERSION_HEADER, "1")
                .header(AUTHORIZATION, testAccessToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .multiPart("prTags", "ENFJ")
                .filter(document("patch-prtag", getPrTagsRequestPartBodySnippet()))
                .when()
                .patch("/personal-info/prTags")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    RequestPartsSnippet getPrTagsRequestPartBodySnippet() {
        return requestParts(
                partWithName("prTags").attributes(constraints("ARRAY")).description("PR태그 목록"));
    }

    @Test
    @DisplayName("프로필 이미지 수정하기")
    void 프로필_이미지_수정하기() {
        userRepository.save(socialUserInfoEntity(ProviderType.KAKAO));

        given(this.spec)
                .log()
                .all()
                .port(port)
                .header(API_VERSION_HEADER, "1")
                .header(AUTHORIZATION, testAccessToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .multiPart("profileImage", "profileImage.jpg", "image/jpeg".getBytes())
                .filter(document("patch-profile", getProfileImageRequestPartBodySnippet()))
                .when()
                .patch("/personal-info/profile")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    RequestPartsSnippet getProfileImageRequestPartBodySnippet() {
        return requestParts(
                partWithName("profileImage")
                        .attributes(constraints("multipart/form-data"))
                        .description("회원 프로필 이미지"));
    }

    @Test
    @DisplayName("다른 사람 프로필 조회하기")
    void 다른_사람_프로필_조회하기() {
        UserInfoEntity userInfo = userRepository.save(socialUserInfoEntity(ProviderType.KAKAO));
        userPrTagRepository.save(userPrTagEntity(userInfo.getId(), "ISTP"));
        userPrTagRepository.save(userPrTagEntity(userInfo.getId(), "보드게임 좋아"));
        userPrTagRepository.save(userPrTagEntity(userInfo.getId(), "눈치빠름"));

        given(this.spec)
                .log()
                .all()
                .port(port)
                .header(API_VERSION_HEADER, "1")
                .header(AUTHORIZATION, testAccessToken)
                .pathParam("userId", userInfo.getId())
                .filter(
                        document(
                                "get-other-personal-info",
                                getPathParametersSnippet(),
                                getOtherPersonalInfoResponseFieldsSnippet()))
                .when()
                .get("/personal-info/{userId}", userInfo.getId())
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    private PathParametersSnippet getPathParametersSnippet() {
        return pathParameters(parameterWithName("userId").description("회원 고유ID"));
    }

    private ResponseFieldsSnippet getOtherPersonalInfoResponseFieldsSnippet() {
        return responseFields(
                fieldWithPath("nickName").type(JsonFieldType.STRING).description("닉네임"),
                fieldWithPath("profileImage").type(JsonFieldType.STRING).description("프로필 이미지"),
                fieldWithPath("averageRating").type(JsonFieldType.NUMBER).description("평균 별점"),
                fieldWithPath("meetingCount").type(JsonFieldType.NUMBER).description("모임 참가 횟수"),
                fieldWithPath("prTags")
                        .type(JsonFieldType.ARRAY)
                        .description("PR태그(없을 경우 빈배열 반환)"));
    }
}
