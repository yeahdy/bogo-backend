package com.boardgo.integration.meeting.controller;

import static com.boardgo.common.constant.HeaderConstant.*;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.*;

import com.boardgo.domain.meeting.controller.request.MeetingCreateRequest;
import com.boardgo.domain.user.repository.UserRepository;
import com.boardgo.integration.init.TestBoardGameInitializer;
import com.boardgo.integration.init.TestMeetingInitializer;
import com.boardgo.integration.init.TestUserInfoInitializer;
import com.boardgo.integration.support.RestDocsTestSupport;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDateTime;
import java.util.Arrays;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

public class MeetingDocsTest extends RestDocsTestSupport {

    @Autowired private TestUserInfoInitializer testUserInfoInitializer;
    @Autowired private TestBoardGameInitializer testBoardGameInitializer;
    @Autowired private TestMeetingInitializer testMeetingInitializer;
    @Autowired private UserRepository userRepository;

    @Test
    @DisplayName("사용자는 모임을 만들 수 있다")
    void 사용자는_모임을_만들_수_있다() throws JsonProcessingException {
        // 사용자 생성
        MeetingCreateRequest request =
                new MeetingCreateRequest(
                        "Test Meeting",
                        "FREE",
                        10,
                        "title",
                        "Seoul",
                        "Gangnam",
                        "37.5665",
                        "126.9780",
                        "detailAddress",
                        "location",
                        LocalDateTime.now().plusDays(1),
                        Arrays.asList(1L, 2L),
                        Arrays.asList(1L, 2L));

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String requestJson = objectMapper.writeValueAsString(request);

        given(this.spec)
                .port(port)
                .log()
                .all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .header(API_VERSION_HEADER, "1")
                .header(AUTHORIZATION, testAccessToken)
                .multiPart("meetingCreateRequest", requestJson, MediaType.APPLICATION_JSON_VALUE)
                .multiPart("image", "test-image.jpg", "image/jpeg".getBytes())
                .filter(
                        document(
                                "meeting-create",
                                requestParts(
                                        partWithName("meetingCreateRequest")
                                                .description("Meeting creation details"),
                                        partWithName("image")
                                                .description("Meeting image file")
                                                .optional()),
                                requestPartFields(
                                        "meetingCreateRequest",
                                        fieldWithPath("content")
                                                .type(JsonFieldType.STRING)
                                                .description("모임 내용"),
                                        fieldWithPath("type")
                                                .type(JsonFieldType.STRING)
                                                .description("모임 타입 (FREE or ACCEPT)"),
                                        fieldWithPath("limitParticipant")
                                                .type(JsonFieldType.NUMBER)
                                                .description("최대 참가자 수"),
                                        fieldWithPath("title")
                                                .type(JsonFieldType.STRING)
                                                .description("모임 제목"),
                                        fieldWithPath("city")
                                                .type(JsonFieldType.STRING)
                                                .description("모임 도시"),
                                        fieldWithPath("county")
                                                .type(JsonFieldType.STRING)
                                                .description("모임 구(ex. 강남구, 성동구)"),
                                        fieldWithPath("latitude")
                                                .type(JsonFieldType.STRING)
                                                .description("모임 장소의 위도"),
                                        fieldWithPath("longitude")
                                                .type(JsonFieldType.STRING)
                                                .description("모임 장소의 경도"),
                                        fieldWithPath("detailAddress")
                                                .type(JsonFieldType.STRING)
                                                .description("모임 장소의 상세주소"),
                                        fieldWithPath("locationName")
                                                .type(JsonFieldType.STRING)
                                                .description("모임 장소의 이름"),
                                        fieldWithPath("meetingDatetime")
                                                .type(JsonFieldType.STRING)
                                                .description("모임 시간"),
                                        fieldWithPath("boardGameIdList")
                                                .type(JsonFieldType.ARRAY)
                                                .description(
                                                        "보드게임 id 리스트(배열) / 개발 서버에서는 더미 데이터 [1 ~ 10]까지 존재"),
                                        fieldWithPath("genreIdList")
                                                .type(JsonFieldType.ARRAY)
                                                .description(
                                                        "보드게임 장르 id 리스트(배열) / 개발 서버에서는 더미 데이터 [1 ~ 10]까지 존재")),
                                responseHeaders(
                                        headerWithName("Location")
                                                .description("만들어진 모임 URI")
                                                .optional())))
                .when()
                .post("/meeting")
                .then()
                .log()
                .all()
                .statusCode(201)
                .header("Location", matchesPattern("/meeting/\\d+"));
    }

    @Test
    @DisplayName("사용자는 모임 목록을 조회할 수 있다")
    void 사용자는_모임_목록을_조회할_수_있다() {
        initEssentialData();
        given(this.spec)
                .log()
                .all()
                .port(port)
                .header(API_VERSION_HEADER, "1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .filter(
                        document(
                                "meeting-list",
                                queryParameters(
                                        parameterWithName("count")
                                                .description("현재 검색 조건의 전체 개수(캐싱을 위함)")
                                                .optional(),
                                        parameterWithName("tag").description("태그 필터").optional(),
                                        parameterWithName("startDate")
                                                .description("모임 날짜 검색을 위한 시작 날짜")
                                                .optional(),
                                        parameterWithName("endDate")
                                                .description("모임 날짜 검색을 위한 마지막 날짜")
                                                .optional(),
                                        parameterWithName("searchWord")
                                                .description("검색어")
                                                .optional(),
                                        parameterWithName("searchType")
                                                .description(
                                                        "검색 타입: TITLE, CONTENT, ALL(제목 or 내용 중 포함되면 가져옴)")
                                                .optional(),
                                        parameterWithName("city").description("도시 필터").optional(),
                                        parameterWithName("county")
                                                .description("County 필터")
                                                .optional(),
                                        parameterWithName("page")
                                                .description("페이지 위치 ex) 1, 2, 3 ...")
                                                .optional(),
                                        parameterWithName("size")
                                                .description("페이지 크기 (만약을 대비해서)")
                                                .optional(),
                                        parameterWithName("sortBy")
                                                .description(
                                                        "정렬 기준: MEETING_DATE, PARTICIPANT_COUNT")
                                                .optional()),
                                responseFields(
                                        fieldWithPath("content[]")
                                                .type(JsonFieldType.ARRAY)
                                                .description("content")
                                                .optional(),
                                        fieldWithPath("content[].id")
                                                .type(JsonFieldType.NUMBER)
                                                .description("모임 ID"),
                                        fieldWithPath("content[].title")
                                                .type(JsonFieldType.STRING)
                                                .description("모임 제목"),
                                        fieldWithPath("content[].city")
                                                .type(JsonFieldType.STRING)
                                                .description("도시"),
                                        fieldWithPath("content[].county")
                                                .type(JsonFieldType.STRING)
                                                .description("county..(군, 구)"),
                                        fieldWithPath("content[].meetingDate")
                                                .type(JsonFieldType.STRING)
                                                .description("모임 시간"),
                                        fieldWithPath("content[].limitParticipant")
                                                .type(JsonFieldType.NUMBER)
                                                .description("최대 참가자 수"),
                                        fieldWithPath("content[].nickName")
                                                .type(JsonFieldType.STRING)
                                                .description("모임 개설자 닉네임"),
                                        fieldWithPath("content[].thumbnail")
                                                .type(JsonFieldType.STRING)
                                                .description("모임 썸네일 URI"),
                                        fieldWithPath("content[].games")
                                                .type(JsonFieldType.ARRAY)
                                                .description("보드게임 제목들"),
                                        fieldWithPath("content[].tags")
                                                .type(JsonFieldType.ARRAY)
                                                .description("태그(게임의 장르)"),
                                        fieldWithPath("content[].participantCount")
                                                .type(JsonFieldType.NUMBER)
                                                .description("현재 참가자 수"),
                                        fieldWithPath("content[].likeStatus")
                                                .type(JsonFieldType.STRING)
                                                .description("찜 상태 여부 -> (Y, N)"),
                                        fieldWithPath("content[].viewCount")
                                                .type(JsonFieldType.NUMBER)
                                                .description("조회 수"),
                                        fieldWithPath("totalElements")
                                                .type(JsonFieldType.NUMBER)
                                                .description("전체 개수 (요청 때 count에 넣어주시면 캐싱 됩니다..!)"),
                                        fieldWithPath("totalPages")
                                                .type(JsonFieldType.NUMBER)
                                                .description("전체 페이지 수"),
                                        fieldWithPath("number")
                                                .type(JsonFieldType.NUMBER)
                                                .description("현재 페이지"),
                                        fieldWithPath("size")
                                                .type(JsonFieldType.NUMBER)
                                                .description("한 페이지 당 개수"),
                                        fieldWithPath("sort.empty")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("정렬 조건이 비어있는지 여부"),
                                        fieldWithPath("sort.sorted")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("content가 정렬되어 있는지의 여부"),
                                        fieldWithPath("sort.unsorted")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("정렬이 안되어있는지의 여부"),
                                        fieldWithPath("first")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("가장 첫번째 페이지의 숫자"),
                                        fieldWithPath("last")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("가장 마지막 페이지의 숫자"),
                                        fieldWithPath("numberOfElements")
                                                .type(JsonFieldType.NUMBER)
                                                .description("현재 페이지에 있는 content.size()"),
                                        fieldWithPath("empty")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("페이지가 비어있는지 여부"),
                                        fieldWithPath("pageable.pageNumber")
                                                .type(JsonFieldType.NUMBER)
                                                .description("현재 페이지 번호"),
                                        fieldWithPath("pageable.pageSize")
                                                .type(JsonFieldType.NUMBER)
                                                .description("페이지 당 항목 수"),
                                        fieldWithPath("pageable.offset")
                                                .type(JsonFieldType.NUMBER)
                                                .description("현재 페이지의 시작 지점"),
                                        fieldWithPath("pageable.paged")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("페이지가 페이징 되었는지 여부"),
                                        fieldWithPath("pageable.unpaged")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("페이지가 페이징 되지 않았는지 여부"),
                                        fieldWithPath("pageable.sort.empty")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("정렬 조건이 비어있는지 여부"),
                                        fieldWithPath("pageable.sort.sorted")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("정렬이 되었는지 여부"),
                                        fieldWithPath("pageable.sort.unsorted")
                                                .type(JsonFieldType.BOOLEAN)
                                                .description("정렬되지 않았는지 여부"))))
                .when()
                .get("/meeting")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("사용자는 모임 상세 조회를 할 수 있다")
    void 사용자는_모임_상세_조회를_할_수_있다() {
        initEssentialData();
        given(this.spec)
                .log()
                .all()
                .port(port)
                .header(API_VERSION_HEADER, "1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParams("id", 10)
                .filter(
                        document(
                                "meeting-detail",
                                pathParameters(parameterWithName("id").description("모임 id")),
                                responseFields(
                                        fieldWithPath("meetingId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("모임 id"),
                                        fieldWithPath("rating")
                                                .type(JsonFieldType.NUMBER)
                                                .description("모임 개설자의 평균 점수"),
                                        fieldWithPath("title")
                                                .type(JsonFieldType.STRING)
                                                .description("모임 제목"),
                                        fieldWithPath("content")
                                                .type(JsonFieldType.STRING)
                                                .description("모임 내용"),
                                        fieldWithPath("likeStatus")
                                                .type(JsonFieldType.STRING)
                                                .description("모임 찜 상태 -> (Y, N) 둘 중 하나"),
                                        fieldWithPath("city")
                                                .type(JsonFieldType.STRING)
                                                .description("도시"),
                                        fieldWithPath("county")
                                                .type(JsonFieldType.STRING)
                                                .description("county..(군, 구)"),
                                        fieldWithPath("longitude")
                                                .type(JsonFieldType.STRING)
                                                .description("경도"),
                                        fieldWithPath("latitude")
                                                .type(JsonFieldType.STRING)
                                                .description("위도"),
                                        fieldWithPath("thumbnail")
                                                .type(JsonFieldType.STRING)
                                                .description("모임 썸네일"),
                                        fieldWithPath("detailAddress")
                                                .type(JsonFieldType.STRING)
                                                .description("모임 장소의 상세주소"),
                                        fieldWithPath("locationName")
                                                .type(JsonFieldType.STRING)
                                                .description("모임 장소의 이름"),
                                        fieldWithPath("meetingDatetime")
                                                .type(JsonFieldType.STRING)
                                                .description("모임 시간"),
                                        fieldWithPath("limitParticipant")
                                                .type(JsonFieldType.NUMBER)
                                                .description("최대 참가자 수"),
                                        fieldWithPath("viewCount")
                                                .type(JsonFieldType.NUMBER)
                                                .description("조회 수"),
                                        fieldWithPath("userNickName")
                                                .type(JsonFieldType.STRING)
                                                .description("모임 개설자 닉네임"),
                                        fieldWithPath("state")
                                                .type(JsonFieldType.STRING)
                                                .description("모임 상태"),
                                        fieldWithPath("genres")
                                                .type(JsonFieldType.ARRAY)
                                                .description("장르들(태그들)"),
                                        fieldWithPath("totalParticipantCount")
                                                .type(JsonFieldType.NUMBER)
                                                .description("모임 참가자 수"),
                                        fieldWithPath("shareCount")
                                                .type(JsonFieldType.NUMBER)
                                                .description("모임 공유 수"),
                                        fieldWithPath("createMeetingCount")
                                                .type(JsonFieldType.NUMBER)
                                                .description("작성자의 모임 개설 횟수"),
                                        fieldWithPath("userParticipantResponseList")
                                                .type(JsonFieldType.ARRAY)
                                                .description("참가자들 중 유저 목록"),
                                        fieldWithPath("userParticipantResponseList[].userId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("참가자 user Id"),
                                        fieldWithPath("userParticipantResponseList[].profileImage")
                                                .type(JsonFieldType.STRING)
                                                .description("참가자 user 프로필 이미지"),
                                        fieldWithPath("userParticipantResponseList[].nickname")
                                                .type(JsonFieldType.STRING)
                                                .description("참가자 user 닉네임"),
                                        fieldWithPath("userParticipantResponseList[].type")
                                                .type(JsonFieldType.STRING)
                                                .description(
                                                        "참가자 user 역할 타입: (LEADER - 모임 생성자, PARTICIPANT - 참여자)"),
                                        fieldWithPath("boardGameListResponseList")
                                                .type(JsonFieldType.ARRAY)
                                                .description("모임 참가자 수"),
                                        fieldWithPath("boardGameListResponseList[].boardGameId")
                                                .type(JsonFieldType.NUMBER)
                                                .description("참가자 boardGame Id"),
                                        fieldWithPath("boardGameListResponseList[].title")
                                                .type(JsonFieldType.STRING)
                                                .description("참가자 boardGame 제목"),
                                        fieldWithPath("boardGameListResponseList[].thumbnail")
                                                .type(JsonFieldType.STRING)
                                                .description("참가자 boardGame 썸네일 URI"))))
                .when()
                .get("/meeting/{id}")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("공유하면 공유횟수가 증가한다")
    void 공유하면_공유횟수가_증가한다() {
        initEssentialData();
        given(this.spec)
                .log()
                .all()
                .port(port)
                .header(API_VERSION_HEADER, "1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParams("id", 10)
                .filter(
                        document(
                                "meeting-share-increment",
                                pathParameters(parameterWithName("id").description("모임 id"))))
                .when()
                .patch("/meeting/share/{id}")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    private void initEssentialData() {
        testBoardGameInitializer.generateBoardGameData();
        testUserInfoInitializer.generateUserData();
        testMeetingInitializer.generateMeetingData();
    }
}
