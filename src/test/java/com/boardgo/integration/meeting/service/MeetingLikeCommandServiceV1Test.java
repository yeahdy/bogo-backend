package com.boardgo.integration.meeting.service;

import com.boardgo.domain.meeting.entity.MeetingLikeEntity;
import com.boardgo.domain.meeting.repository.MeetingLikeRepository;
import com.boardgo.domain.meeting.service.MeetingLikeCommandUseCase;
import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.domain.user.repository.UserRepository;
import com.boardgo.domain.user.service.dto.CustomUserDetails;
import com.boardgo.integration.init.TestBoardGameInitializer;
import com.boardgo.integration.init.TestMeetingInitializer;
import com.boardgo.integration.init.TestUserInfoInitializer;
import com.boardgo.integration.support.IntegrationTestSupport;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class MeetingLikeCommandServiceV1Test extends IntegrationTestSupport {
    @Autowired private MeetingLikeCommandUseCase meetingLikeCommandUseCase;
    @Autowired private MeetingLikeRepository meetingLikeRepository;

    @Autowired private TestUserInfoInitializer testUserInfoInitializer;
    @Autowired private TestBoardGameInitializer testBoardGameInitializer;
    @Autowired private TestMeetingInitializer testMeetingInitializer;
    @Autowired private UserRepository userRepository;

    @Test
    @DisplayName("찜 목록들을 저장할 수 있다")
    void 찜_목록들을_저장할_수_있다() {
        // given
        testBoardGameInitializer.generateBoardGameData();
        setSecurityContext();
        testMeetingInitializer.generateMeetingData();
        List<Long> meetingIdList = List.of(1L, 2L, 3L);
        // when
        meetingLikeCommandUseCase.createMany(meetingIdList);
        // then
        List<MeetingLikeEntity> entityList = meetingLikeRepository.findByUserId(1L);
        Assertions.assertThat(entityList)
                .extracting(MeetingLikeEntity::getMeetingId)
                .containsExactlyInAnyOrderElementsOf(meetingIdList);
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
