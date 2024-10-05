package com.boardgo.integration.notification.controller;

import static com.boardgo.common.constant.HeaderConstant.API_VERSION_HEADER;
import static com.boardgo.common.constant.HeaderConstant.AUTHORIZATION;
import static com.boardgo.integration.data.NotificationData.getNotificationMessage;
import static io.restassured.RestAssured.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import com.boardgo.domain.notification.entity.MessageType;
import com.boardgo.domain.notification.entity.NotificationEntity;
import com.boardgo.domain.notification.repository.NotificationRepository;
import com.boardgo.integration.data.NotificationData;
import com.boardgo.integration.support.RestDocsTestSupport;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class NotificationRestDocs extends RestDocsTestSupport {
    @Autowired private NotificationRepository notificationRepository;

    @Test
    @DisplayName("회원의 알림 목록을 조회한다")
    void 회원의_알림_목록을_조회한다() {
        NotificationEntity notification1 =
                NotificationData.getNotification(
                                1L, getNotificationMessage(MessageType.MEETING_MODIFY).build())
                        .build();
        NotificationEntity notification2 =
                NotificationData.getNotification(
                                1L, getNotificationMessage(MessageType.REQUEST_REVIEW).build())
                        .isRead(true)
                        .pathUrl("/mypage/review")
                        .build();
        NotificationEntity notification3 =
                NotificationData.getNotification(
                                1L, getNotificationMessage(MessageType.REVIEW_RECEIVED).build())
                        .isRead(true)
                        .pathUrl("/mypage/review/receivedReviews")
                        .sendDateTime(LocalDateTime.of(2024, 10, 4, 15, 0))
                        .build();

        notificationRepository.save(notification1);
        notificationRepository.save(notification2);
        notificationRepository.save(notification3);

        given(this.spec)
                .port(port)
                .log()
                .all()
                .header(API_VERSION_HEADER, "1")
                .header(AUTHORIZATION, testAccessToken)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .filter(document("get-notifications", getNotificationResponseFieldsSnippet()))
                .when()
                .get("/notification/list")
                .then()
                .statusCode(HttpStatus.OK.value())
                .log()
                .ifError()
                .extract();
    }

    private ResponseFieldsSnippet getNotificationResponseFieldsSnippet() {
        return responseFields(
                List.of(
                        fieldWithPath("[].notificationId")
                                .type(JsonFieldType.NUMBER)
                                .description("알림 고유 Id"),
                        fieldWithPath("[].title").type(JsonFieldType.STRING).description("알림 제목"),
                        fieldWithPath("[].content").type(JsonFieldType.STRING).description("알림 내용"),
                        fieldWithPath("[].isRead").type(JsonFieldType.BOOLEAN).description("확인 여부"),
                        fieldWithPath("[].pathUrl")
                                .type(JsonFieldType.STRING)
                                .description("알림 이동 경로")));
    }
}
