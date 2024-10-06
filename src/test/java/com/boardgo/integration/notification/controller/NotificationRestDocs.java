package com.boardgo.integration.notification.controller;

import static com.boardgo.common.constant.HeaderConstant.API_VERSION_HEADER;
import static com.boardgo.common.constant.HeaderConstant.AUTHORIZATION;
import static com.boardgo.integration.data.NotificationData.getNotification;
import static com.boardgo.integration.data.NotificationData.getNotificationMessage;
import static io.restassured.RestAssured.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import com.boardgo.domain.notification.entity.MessageType;
import com.boardgo.domain.notification.entity.NotificationEntity;
import com.boardgo.domain.notification.entity.NotificationMessage;
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
import org.springframework.restdocs.request.QueryParametersSnippet;

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

    @Test
    @DisplayName("알림메세지 읽기")
    void 알림메세지_읽기() {
        // 발송
        NotificationMessage message = getNotificationMessage(MessageType.MEETING_REMINDER).build();
        for (int i = 0; i < 5; i++) {
            notificationRepository.save(
                    getNotification(1L, message).pathUrl("/gatherings/" + i + 1).build());
        }

        given(this.spec)
                .log()
                .all()
                .port(port)
                .header(API_VERSION_HEADER, "1")
                .header(AUTHORIZATION, testAccessToken)
                .queryParam("ids", "1,2,3,4,5")
                .filter(document("patch-read-notification", getNotificationQueryParamSnippet()))
                .when()
                .patch("/notification/read")
                .then()
                .statusCode(HttpStatus.OK.value())
                .log()
                .ifError()
                .extract();
    }

    QueryParametersSnippet getNotificationQueryParamSnippet() {
        return queryParameters(
                parameterWithName("ids").description("알림 ID ARRAY 타입 ids=1,2,3,4,5"));
    }
}
