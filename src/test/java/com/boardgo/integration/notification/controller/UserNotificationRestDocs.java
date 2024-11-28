package com.boardgo.integration.notification.controller;

import static com.boardgo.common.constant.HeaderConstant.API_VERSION_HEADER;
import static com.boardgo.common.constant.HeaderConstant.AUTHORIZATION;
import static com.boardgo.domain.notification.entity.MessageType.REQUEST_REVIEW;
import static com.boardgo.integration.data.TermsConditionsData.getTermsConditions;
import static com.boardgo.integration.data.UserInfoData.userInfoEntityData;
import static com.boardgo.integration.fixture.NotificationSettingFixture.getNotificationSettings;
import static io.restassured.RestAssured.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import com.boardgo.domain.notification.controller.request.UserNotificationSettingUpdateRequest;
import com.boardgo.domain.notification.entity.NotificationSettingEntity;
import com.boardgo.domain.notification.entity.UserNotificationSettingEntity;
import com.boardgo.domain.notification.repository.NotificationSettingRepository;
import com.boardgo.domain.notification.repository.UserNotificationSettingRepository;
import com.boardgo.domain.termsconditions.entity.TermsConditionsEntity;
import com.boardgo.domain.termsconditions.entity.UserTermsConditionsEntity;
import com.boardgo.domain.termsconditions.entity.enums.TermsConditionsType;
import com.boardgo.domain.termsconditions.repository.TermsConditionsRepository;
import com.boardgo.domain.termsconditions.repository.UserTermsConditionsRepository;
import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.domain.user.repository.UserRepository;
import com.boardgo.integration.support.RestDocsTestSupport;
import io.restassured.http.ContentType;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class UserNotificationRestDocs extends RestDocsTestSupport {

    @Autowired UserRepository userRepository;
    @Autowired NotificationSettingRepository notificationSettingRepository;
    @Autowired UserNotificationSettingRepository userNotificationSettingRepository;
    @Autowired private TermsConditionsRepository termsConditionsRepository;
    @Autowired private UserTermsConditionsRepository userTermsConditionsRepository;

    private final List<NotificationSettingEntity> notificationSettings = getNotificationSettings();

    @BeforeEach
    void init() {
        notificationSettingRepository.saveAll(notificationSettings);
        UserInfoEntity user = userInfoEntityData("user1@naver.com", "user1").build();
        userRepository.save(user);

        notificationSettings.forEach(
                notificationSettingEntity -> {
                    boolean isAgreed = true;
                    if (REQUEST_REVIEW.equals(notificationSettingEntity.getMessageType())) {
                        isAgreed = false;
                    }
                    userNotificationSettingRepository.save(
                            UserNotificationSettingEntity.builder()
                                    .userInfoId(user.getId())
                                    .notificationSetting(notificationSettingEntity)
                                    .isAgreed(isAgreed)
                                    .build());
                });
    }

    @Test
    @DisplayName("회원의 알림설정 목록을 조회한다")
    void 회원의_알림설정_목록을_조회한다() {
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
                                "get-user-notifications",
                                getUserNotificationResponseFieldsSnippet()))
                .when()
                .get("/user-notification/list")
                .then()
                .statusCode(HttpStatus.OK.value())
                .log()
                .ifError()
                .extract();
    }

    private ResponseFieldsSnippet getUserNotificationResponseFieldsSnippet() {
        return responseFields(
                List.of(
                        fieldWithPath("[].isAgreed")
                                .type(JsonFieldType.BOOLEAN)
                                .description("알림설정 ON/OFF"),
                        fieldWithPath("[].content").type(JsonFieldType.STRING).description("알림 내용"),
                        fieldWithPath("[].additionalContent")
                                .type(JsonFieldType.STRING)
                                .description("알림 부가설명"),
                        fieldWithPath("[].messageType")
                                .type(JsonFieldType.STRING)
                                .description("알림설정 타입")));
    }

    @Test
    @DisplayName("회원의 알림 설정을 수정한다")
    void 회원의_알림_설정을_수정한다() {
        // given
        UserNotificationSettingUpdateRequest request =
                new UserNotificationSettingUpdateRequest(REQUEST_REVIEW, Boolean.TRUE);
        // 회원 푸시약관동의 활성화
        TermsConditionsEntity termsConditionsEntity =
                getTermsConditions(TermsConditionsType.PUSH).required(false).build();
        termsConditionsRepository.save(termsConditionsEntity);
        userTermsConditionsRepository.save(
                UserTermsConditionsEntity.builder()
                        .userInfoId(1L)
                        .termsConditionsEntity(termsConditionsEntity)
                        .agreement(Boolean.TRUE)
                        .build());

        given(this.spec)
                .log()
                .all()
                .port(port)
                .header(API_VERSION_HEADER, "1")
                .header(AUTHORIZATION, testAccessToken)
                .contentType(ContentType.JSON)
                .filter(
                        document(
                                "patch-user-notification",
                                getUserNotificationRequestFieldsSnippet()))
                .body(writeValueAsString(request))
                .when()
                .patch("/user-notification")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    private RequestFieldsSnippet getUserNotificationRequestFieldsSnippet() {
        return requestFields(
                fieldWithPath("messageType").type(JsonFieldType.STRING).description("알림설정 타입"),
                fieldWithPath("isAgreed").type(JsonFieldType.BOOLEAN).description("동의여부"));
    }
}
