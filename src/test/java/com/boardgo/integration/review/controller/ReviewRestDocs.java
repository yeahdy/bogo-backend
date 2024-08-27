package com.boardgo.integration.review.controller;

import static com.boardgo.common.constant.HeaderConstant.API_VERSION_HEADER;
import static com.boardgo.common.constant.HeaderConstant.AUTHORIZATION;
import static com.boardgo.integration.fixture.UserInfoFixture.localUserInfoEntity;
import static com.boardgo.integration.fixture.UserInfoFixture.socialUserInfoEntity;
import static io.restassured.RestAssured.given;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import com.boardgo.domain.meeting.entity.MeetingEntity;
import com.boardgo.domain.meeting.entity.enums.MeetingState;
import com.boardgo.domain.meeting.repository.MeetingRepository;
import com.boardgo.domain.review.controller.request.ReviewCreateRequest;
import com.boardgo.domain.user.entity.enums.ProviderType;
import com.boardgo.domain.user.repository.UserRepository;
import com.boardgo.integration.init.TestMeetingInitializer;
import com.boardgo.integration.support.RestDocsTestSupport;
import java.util.List;
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

public class ReviewRestDocs extends RestDocsTestSupport {

    @Autowired private TestMeetingInitializer testMeetingInitializer;
    @Autowired private UserRepository userRepository;

    @Autowired private MeetingRepository meetingRepository;

    @Test
    @DisplayName("리뷰 모임 목록 조회하기")
    void 리뷰_모임_목록_조회하기() {
        userRepository.save(socialUserInfoEntity(ProviderType.GOOGLE));
        List<Long> meetingIds = testMeetingInitializer.generateMeetingData();
        List<MeetingEntity> meetingEntities = meetingRepository.findByIdIn(meetingIds);
        for (int i = 0; i < meetingEntities.size() / 10; i++) {
            MeetingEntity meeting = meetingEntities.get(i);
            meeting.updateMeetingState(MeetingState.FINISH);
            meetingRepository.save(meeting);
        }

        given(this.spec)
                .log()
                .all()
                .port(port)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .header(API_VERSION_HEADER, "1")
                .header(AUTHORIZATION, testAccessToken)
                .multiPart("reviewType", "PRE_PROGRESS")
                .filter(
                        document(
                                "get-review-meetings",
                                getReviewTypeRequestPartBodySnippet(),
                                getReviewMeetingsResponseFieldsSnippet()))
                .when()
                .get("/my/review/meetings")
                .then()
                .log()
                .ifError()
                .statusCode(HttpStatus.OK.value());
    }

    private RequestPartsSnippet getReviewTypeRequestPartBodySnippet() {
        return requestParts(partWithName("reviewType").description("상황별 타입(PRE_PROGRESS/FINISH)"));
    }

    private ResponseFieldsSnippet getReviewMeetingsResponseFieldsSnippet() {
        return responseFields(
                List.of(
                        fieldWithPath("[].meetingId")
                                .type(JsonFieldType.NUMBER)
                                .description("모임 고유Id"),
                        fieldWithPath("[].title").type(JsonFieldType.STRING).description("모임 제목"),
                        fieldWithPath("[].thumbnail")
                                .type(JsonFieldType.STRING)
                                .description("모임 썸네일"),
                        fieldWithPath("[].city").type(JsonFieldType.STRING).description("도시"),
                        fieldWithPath("[].county")
                                .type(JsonFieldType.STRING)
                                .description("모임 구(ex. 강남구, 성동구)"),
                        fieldWithPath("[].meetingDate")
                                .type(JsonFieldType.STRING)
                                .description("모임 날짜")));
    }

    @Test
    @DisplayName("리뷰 작성하기")
    void 리뷰_작성하기() {
        userRepository.save(socialUserInfoEntity(ProviderType.GOOGLE));
        userRepository.save(localUserInfoEntity());
        testMeetingInitializer.generateMeetingData();
        MeetingEntity meeting = meetingRepository.findById(30L).get();
        meeting.updateMeetingState(MeetingState.FINISH);
        meetingRepository.save(meeting);
        ReviewCreateRequest request =
                new ReviewCreateRequest(2L, meeting.getId(), 5, List.of(1L, 2L, 4L, 9L, 10L));

        given(this.spec)
                .port(port)
                .log()
                .all()
                .header(API_VERSION_HEADER, "1")
                .header(AUTHORIZATION, testAccessToken)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .filter(document("create-review", getReviewCreateRequestFieldsSnippet()))
                .body(writeValueAsString(request))
                .urlEncodingEnabled(false)
                .when()
                .post("/my/review")
                .then()
                .log()
                .ifError()
                .statusCode(HttpStatus.CREATED.value())
                .extract();
    }

    private RequestFieldsSnippet getReviewCreateRequestFieldsSnippet() {
        return requestFields(
                fieldWithPath("revieweeId").type(NUMBER).description("리뷰 받은 참여자 고유Id"),
                fieldWithPath("meetingId").type(NUMBER).description("모임 고유Id"),
                fieldWithPath("rating").type(NUMBER).description("평점"),
                fieldWithPath("evaluationTagList").type(ARRAY).description("평가태그 목록"));
    }

    @Test
    @DisplayName("작성할 리뷰 참여자 목록 조회하기")
    void 작성할_리뷰_참여자_목록_조회하기() {
        userRepository.save(socialUserInfoEntity(ProviderType.GOOGLE));
        userRepository.save(localUserInfoEntity());
        testMeetingInitializer.generateMeetingData();
        Long meetingId = 2L;
        MeetingEntity meeting = meetingRepository.findById(meetingId).get();
        meeting.updateMeetingState(MeetingState.FINISH);
        meetingRepository.save(meeting);

        given(this.spec)
                .log()
                .all()
                .port(port)
                .header(API_VERSION_HEADER, "1")
                .header(AUTHORIZATION, testAccessToken)
                .pathParam("meetingId", meetingId)
                .filter(
                        document(
                                "get-review-meeting-Participants",
                                getPathParametersSnippet(),
                                getParticipantsResponseFieldsSnippet()))
                .when()
                .get("/my/review/meetings/{meetingId}", meetingId)
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    private PathParametersSnippet getPathParametersSnippet() {
        return pathParameters(parameterWithName("meetingId").description("모임 고유Id"));
    }

    private ResponseFieldsSnippet getParticipantsResponseFieldsSnippet() {
        return responseFields(
                List.of(
                        fieldWithPath("[].revieweeId")
                                .type(JsonFieldType.NUMBER)
                                .description("리뷰를 받는 참여자 Id"),
                        fieldWithPath("[].revieweeName")
                                .type(JsonFieldType.STRING)
                                .description("리뷰를 받는 참여자 닉네임")));
    }
}
