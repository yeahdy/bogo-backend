package com.boardgo.integration.notification.controller;

import static com.boardgo.common.constant.HeaderConstant.API_VERSION_HEADER;
import static com.boardgo.common.constant.HeaderConstant.AUTHORIZATION;
import static com.boardgo.domain.notification.entity.MessageType.REQUEST_REVIEW;
import static com.boardgo.integration.data.UserInfoData.userInfoEntityData;
import static com.boardgo.integration.fixture.NotificationSettingFixture.getNotificationSettings;
import static io.restassured.RestAssured.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import com.boardgo.domain.notification.entity.NotificationSettingEntity;
import com.boardgo.domain.notification.entity.UserNotificationSettingEntity;
import com.boardgo.domain.notification.repository.NotificationSettingRepository;
import com.boardgo.domain.notification.repository.UserNotificationSettingRepository;
import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.domain.user.repository.UserRepository;
import com.boardgo.integration.support.RestDocsTestSupport;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class UserNotificationRestDocs extends RestDocsTestSupport {

    @Autowired UserRepository userRepository;
    @Autowired NotificationSettingRepository notificationSettingRepository;
    @Autowired UserNotificationSettingRepository userNotificationSettingRepository;
    private final List<NotificationSettingEntity> notificationSettings = getNotificationSettings();

    @BeforeEach
    void init() {
        notificationSettingRepository.saveAll(notificationSettings);
    }

    @Test
    @DisplayName("회원의 알림설정 목록을 조회한다")
    void 회원의_알림설정_목록을_조회한다() {
        // given
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
}
