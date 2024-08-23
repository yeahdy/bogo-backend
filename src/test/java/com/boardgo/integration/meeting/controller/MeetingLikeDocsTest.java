package com.boardgo.integration.meeting.controller;

import static com.boardgo.common.constant.HeaderConstant.*;
import static io.restassured.RestAssured.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.*;

import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.domain.user.repository.UserRepository;
import com.boardgo.domain.user.service.dto.CustomUserDetails;
import com.boardgo.integration.init.TestBoardGameInitializer;
import com.boardgo.integration.init.TestMeetingInitializer;
import com.boardgo.integration.init.TestUserInfoInitializer;
import com.boardgo.integration.support.RestDocsTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class MeetingLikeDocsTest extends RestDocsTestSupport {
    @Autowired private TestUserInfoInitializer testUserInfoInitializer;
    @Autowired private TestBoardGameInitializer testBoardGameInitializer;
    @Autowired private TestMeetingInitializer testMeetingInitializer;
    @Autowired private UserRepository userRepository;

    @Test
    @DisplayName("사용자는 찜을 할 수 있다")
    void 사용자는_찜을_할_수_있다() {
        testBoardGameInitializer.generateBoardGameData();
        setSecurityContext();
        testMeetingInitializer.generateMeetingData();

        given(this.spec)
                .log()
                .all()
                .port(port)
                .header(API_VERSION_HEADER, "1")
                .header(AUTHORIZATION, testAccessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .queryParam("meetingIdList", "1,2,3")
                .filter(
                        document(
                                "meeting-like",
                                queryParameters(
                                        parameterWithName("meetingIdList")
                                                .description(
                                                        "한개만 보내도 되고, 여러개를 콤마(,)찍어서 보내도 됩니다! ex) meetingIdList=1,2,3"))))
                .when()
                .post("/meeting/like")
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    private void setSecurityContext() {
        testUserInfoInitializer.generateUserData();
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        UserInfoEntity userInfoEntity =
                userRepository
                        .findById(1L)
                        .orElseThrow(() -> new RuntimeException("User not found"));
        CustomUserDetails customUserDetails = new CustomUserDetails(userInfoEntity);

        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        customUserDetails, "password1", customUserDetails.getAuthorities());
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
    }
}
