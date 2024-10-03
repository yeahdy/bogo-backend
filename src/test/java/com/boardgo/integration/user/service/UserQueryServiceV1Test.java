package com.boardgo.integration.user.service;

import static com.boardgo.integration.data.UserInfoData.*;
import static com.boardgo.integration.fixture.MeetingParticipantFixture.*;
import static com.boardgo.integration.fixture.ReviewFixture.*;
import static com.boardgo.integration.fixture.UserInfoFixture.*;
import static com.boardgo.integration.fixture.UserPrTagFixture.*;
import static org.assertj.core.api.Assertions.*;

import com.boardgo.common.exception.CustomIllegalArgumentException;
import com.boardgo.common.exception.CustomNullPointException;
import com.boardgo.domain.meeting.repository.MeetingParticipantRepository;
import com.boardgo.domain.review.repository.ReviewRepository;
import com.boardgo.domain.user.controller.request.EmailRequest;
import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.domain.user.entity.UserPrTagEntity;
import com.boardgo.domain.user.entity.enums.ProviderType;
import com.boardgo.domain.user.repository.UserPrTagRepository;
import com.boardgo.domain.user.repository.UserRepository;
import com.boardgo.domain.user.service.UserQueryUseCase;
import com.boardgo.domain.user.service.facade.UserQueryServiceFacade;
import com.boardgo.domain.user.service.response.OtherPersonalInfoResponse;
import com.boardgo.domain.user.service.response.UserInfoResponse;
import com.boardgo.domain.user.service.response.UserPersonalInfoResponse;
import com.boardgo.integration.support.IntegrationTestSupport;
import java.util.List;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

public class UserQueryServiceV1Test extends IntegrationTestSupport {
    @Autowired private UserRepository userRepository;
    @Autowired private UserPrTagRepository userPrTagRepository;
    @Autowired private MeetingParticipantRepository meetingParticipantRepository;
    @Autowired private ReviewRepository reviewRepository;
    @Autowired private UserQueryUseCase userQueryUseCase;
    @Autowired private UserQueryServiceFacade userQueryServiceFacade;

    @Test
    @DisplayName("해당 이메일이 존재하지 않으면 에러가 발생하지 않는다")
    void 해당_이메일이_존재하지_않으면_아무일도_일어나지_않는다() {
        // given
        EmailRequest emailRequest = new EmailRequest("aa@aa.com");
        // when
        // then
        userQueryUseCase.existEmail(emailRequest);
    }

    @Test
    @DisplayName("해당 이메일이 존재하면 error를 반환한다")
    void 해당_이메일이_존재하면_error를_반환한다() {
        // given
        EmailRequest emailRequest = new EmailRequest("ghksdagh@naver.com");
        userRepository.save(localUserInfoEntity());
        // when

        // then
        Assertions.assertThatThrownBy(() -> userQueryUseCase.existEmail(emailRequest))
                .isInstanceOf(CustomIllegalArgumentException.class);
    }

    @Test
    @DisplayName("해당 닉네임이 존재하지 않으면 에러가 발생하지 않는다")
    void 해당_닉네임이_존재하지_않으면_에러가_발생하지_않는다() {
        // given
        String nickname = "nickName";
        // when
        // then
        userQueryUseCase.existNickName(nickname);
    }

    @Test
    @DisplayName("해당 닉네임이 존재하면 에러가 발생한다")
    void 해당_닉네임이_존재하면_에러가_발생한다() {
        // given
        String nickname = "water";
        userRepository.save(localUserInfoEntity());
        // when
        // then
        Assertions.assertThatThrownBy(() -> userQueryUseCase.existNickName(nickname))
                .isInstanceOf(CustomIllegalArgumentException.class);
    }

    @Test
    @DisplayName("회원 조회가 불가할 경우 예외가 발생한다")
    void 회원_조회가_불가할_경우_예외가_발생한다() {
        // given
        Long userId = 14511515L;

        // when
        // then
        assertThatThrownBy(() -> userQueryUseCase.getPersonalInfo(userId))
                .isInstanceOf(CustomNullPointException.class);
    }

    @Test
    @DisplayName("회원정보로 이메일(id), 닉네임, 프로필이미지, 평점, PR태그를 조회한다")
    void 회원정보로_이메일_닉네임_프로필이미지_평점_PR태그를_조회한다() {
        // given
        UserInfoEntity userInfo = userInfoEntityData("aa@aa.com", "nickName").build();
        Long userId = getUserId(userInfo);
        UserPrTagEntity userPrTagEntity1 =
                userPrTagRepository.save(userPrTagEntity(userId, "ENFJ"));
        UserPrTagEntity userPrTagEntity2 =
                userPrTagRepository.save(userPrTagEntity(userId, "반모환영"));
        UserPrTagEntity userPrTagEntity3 =
                userPrTagRepository.save(userPrTagEntity(userId, "보드게임신"));
        reviewRepository.save(getReview(2L, userInfo.getId(), 1L));

        // when
        UserPersonalInfoResponse personalInfo = userQueryServiceFacade.getPersonalInfo(userId);

        // then
        assertThat(personalInfo.email()).isEqualTo(userInfo.getEmail());
        assertThat(personalInfo.nickName()).isEqualTo(userInfo.getNickName());
        assertThat(personalInfo.profileImage()).isEqualTo(userInfo.getProfileImage());
        assertThat(personalInfo.averageRating()).isEqualTo(4.0);
        assertThat(personalInfo.prTags())
                .containsAll(
                        List.of(
                                userPrTagEntity1.getTagName(),
                                userPrTagEntity2.getTagName(),
                                userPrTagEntity3.getTagName()));
    }

    @ParameterizedTest
    @DisplayName("회원정보에 프로필이미지와 PR태그는 없을 수도 있다")
    @MethodSource("getNoImageAndPrtTagsUserEntity")
    void 회원정보에_프로필이미지와_PR태그는_없을_수도_있다(UserInfoEntity userInfo) {
        // given
        Long userId = getUserId(userInfo);

        // when
        UserInfoResponse personalInfo = userQueryUseCase.getPersonalInfo(userId);

        // then
        assertThat(personalInfo.profileImage()).isNull();
        List<UserPrTagEntity> prTags = userPrTagRepository.findByUserInfoId(userId);
        assertThat(prTags).isEmpty();
    }

    @ParameterizedTest
    @DisplayName("다른 사람 프로필 조회 시 회원의 모임 참석 횟수를 알 수 있다")
    @MethodSource("getNoImageAndPrtTagsUserEntity")
    void 다른_사람_프로필_조회_시_회원의_모임_참석_횟수를_알_수_있다(UserInfoEntity userInfo) {
        // given
        Long userId = getUserId(userInfo);
        int participationCount = 3;
        for (long i = 0; i < participationCount; i++) {
            meetingParticipantRepository.save(getParticipantMeetingParticipantEntity(i, userId));
        }

        // when
        OtherPersonalInfoResponse otherPersonalInfo =
                userQueryServiceFacade.getOtherPersonalInfo(userId);

        // then
        assertThat(otherPersonalInfo.meetingCount()).isEqualTo(participationCount);
    }

    private Long getUserId(UserInfoEntity userInfo) {
        UserInfoEntity userInfoEntity = userRepository.save(userInfo);
        return userInfoEntity.getId();
    }

    private static Stream<Arguments> getNoImageAndPrtTagsUserEntity() {
        return Stream.of(
                Arguments.of(
                        UserInfoEntity.builder()
                                .email("aa@aa.com")
                                .password("password")
                                .nickName("nickName")
                                .profileImage(null)
                                .providerType(ProviderType.LOCAL)
                                .build()));
    }
}
