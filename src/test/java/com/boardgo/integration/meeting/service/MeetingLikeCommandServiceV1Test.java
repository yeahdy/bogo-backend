package com.boardgo.integration.meeting.service;

import com.boardgo.common.exception.CustomNoSuchElementException;
import com.boardgo.domain.meeting.entity.MeetingLikeEntity;
import com.boardgo.domain.meeting.repository.MeetingLikeRepository;
import com.boardgo.domain.meeting.service.MeetingLikeCommandUseCase;
import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.domain.user.repository.UserRepository;
import com.boardgo.domain.user.service.response.CustomUserDetails;
import com.boardgo.integration.init.TestBoardGameInitializer;
import com.boardgo.integration.init.TestMeetingInitializer;
import com.boardgo.integration.init.TestUserInfoInitializer;
import com.boardgo.integration.support.IntegrationTestSupport;
import java.util.List;
import java.util.Optional;
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
    @Autowired private UserRepository userRepository;

    @Autowired private TestUserInfoInitializer testUserInfoInitializer;
    @Autowired private TestBoardGameInitializer testBoardGameInitializer;
    @Autowired private TestMeetingInitializer testMeetingInitializer;

    @Test
    @DisplayName("찜 목록들을 저장할 수 있다")
    void 찜_목록들을_저장할_수_있다() {
        // given
        testBoardGameInitializer.generateBoardGameData();
        testUserInfoInitializer.generateUserData();

        setSecurityContext(1L);
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

    @Test
    @DisplayName("찜 목록을 삭제할 수 있다")
    void 찜_목록을_삭제할_수_있다() {
        // given
        testBoardGameInitializer.generateBoardGameData();
        testUserInfoInitializer.generateUserData();

        setSecurityContext(1L);
        testMeetingInitializer.generateMeetingData();
        List<Long> meetingIdList = List.of(1L, 2L, 3L);
        meetingLikeCommandUseCase.createMany(meetingIdList);
        MeetingLikeEntity first =
                meetingLikeRepository.findAll().stream()
                        .findFirst()
                        .orElseThrow(() -> new CustomNoSuchElementException("찜"));
        // when
        meetingLikeCommandUseCase.deleteByMeetingId(1L);
        // then
        Optional<MeetingLikeEntity> deletedEntity = meetingLikeRepository.findById(first.getId());
        Assertions.assertThat(deletedEntity).isEmpty();
    }

    @Test
    @DisplayName("다른 사람의 찜 목록을 삭제할 수 없다")
    void 다른_사람의_찜_목록을_삭제할_수_없다() {
        // given
        testBoardGameInitializer.generateBoardGameData();
        testUserInfoInitializer.generateUserData();

        setSecurityContext(1L);
        testMeetingInitializer.generateMeetingData();
        List<Long> meetingIdList = List.of(1L, 2L, 3L);
        meetingLikeCommandUseCase.createMany(meetingIdList);
        MeetingLikeEntity first =
                meetingLikeRepository.findAll().stream()
                        .findFirst()
                        .orElseThrow(() -> new CustomNoSuchElementException("찜"));
        setSecurityContext(2L);
        // when
        // then
        Assertions.assertThatThrownBy(
                        () -> {
                            meetingLikeCommandUseCase.deleteByMeetingId(1L);
                        })
                .isInstanceOf(CustomNoSuchElementException.class);
    }

    private void setSecurityContext(Long userId) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        UserInfoEntity userInfoEntity =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found"));
        CustomUserDetails customUserDetails = new CustomUserDetails(userInfoEntity);

        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        customUserDetails, "password" + userId, customUserDetails.getAuthorities());
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
    }
}
