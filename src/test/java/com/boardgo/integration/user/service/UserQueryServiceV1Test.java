package com.boardgo.integration.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.boardgo.common.exception.CustomIllegalArgumentException;
import com.boardgo.common.exception.CustomNullPointException;
import com.boardgo.domain.user.controller.dto.EmailRequest;
import com.boardgo.domain.user.controller.dto.NickNameRequest;
import com.boardgo.domain.user.controller.dto.UserPersonalInfoResponse;
import com.boardgo.domain.user.entity.ProviderType;
import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.domain.user.entity.UserPrTagEntity;
import com.boardgo.domain.user.repository.UserPrTagRepository;
import com.boardgo.domain.user.repository.UserRepository;
import com.boardgo.domain.user.service.UserQueryUseCase;
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
    @Autowired private UserQueryUseCase userQueryUseCase;

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
        EmailRequest emailRequest = new EmailRequest("aa@aa.com");
        UserInfoEntity userInfoEntity =
                UserInfoEntity.builder()
                        .email("aa@aa.com")
                        .password("password")
                        .nickName("nickName")
                        .providerType(ProviderType.LOCAL)
                        .build();
        userRepository.save(userInfoEntity);
        // when

        // then
        Assertions.assertThatThrownBy(() -> userQueryUseCase.existEmail(emailRequest))
                .isInstanceOf(CustomIllegalArgumentException.class);
    }

    @Test
    @DisplayName("해당 닉네임이 존재하지 않으면 에러가 발생하지 않는다")
    void 해당_닉네임이_존재하지_않으면_에러가_발생하지_않는다() {
        // given
        NickNameRequest nickNameRequest = new NickNameRequest("nickName");
        // when
        userQueryUseCase.existNickName(nickNameRequest);
        // then

    }

    @Test
    @DisplayName("해당 닉네임이 존재하면 에러가 발생한다")
    void 해당_닉네임이_존재하면_에러가_발생한다() {
        // given
        NickNameRequest nickNameRequest = new NickNameRequest("nickName");
        UserInfoEntity userInfoEntity =
                UserInfoEntity.builder()
                        .email("aa@aa.com")
                        .password("password")
                        .nickName("nickName")
                        .providerType(ProviderType.LOCAL)
                        .build();
        userRepository.save(userInfoEntity);
        // when
        // then
        Assertions.assertThatThrownBy(() -> userQueryUseCase.existNickName(nickNameRequest))
                .isInstanceOf(CustomIllegalArgumentException.class);
    }

    @Test
    @DisplayName("회원정보 조회 시 회원 조회 불가 시 예외가 발생한다")
    void 회원정보_조회_시_회원_조회_불가_시_예외가_발생한다() {
        // given
        Long userId = 14511515L;

        // when
        // then
        assertThatThrownBy(() -> userQueryUseCase.getPersonalInfo(userId))
                .isInstanceOf(CustomNullPointException.class);
    }

    @ParameterizedTest
    @DisplayName("회원정보로 이메일(id), 닉네임, 프로필이미지, 평점, PR태그를 조회한다")
    @MethodSource("getUserEntity")
    void 회원정보로_이메일_닉네임_프로필이미지_평점_PR태그를_조회한다(UserInfoEntity userInfo) {
        // given
        Long userId = getUserId(userInfo);
        List<UserPrTagEntity> prTagEntities =
                List.of(
                        UserPrTagEntity.builder().tagName("ENFJ").userInfoId(userId).build(),
                        UserPrTagEntity.builder().tagName("반모환영").userInfoId(userId).build(),
                        UserPrTagEntity.builder().tagName("보드게임신").userInfoId(userId).build());

        UserPrTagEntity userPrTagEntity1 = userPrTagRepository.save(prTagEntities.get(0));
        UserPrTagEntity userPrTagEntity2 = userPrTagRepository.save(prTagEntities.get(1));
        UserPrTagEntity userPrTagEntity3 = userPrTagRepository.save(prTagEntities.get(2));

        // when
        UserPersonalInfoResponse personalInfo = userQueryUseCase.getPersonalInfo(userId);

        // then
        assertThat(personalInfo.email()).isEqualTo(userInfo.getEmail());
        assertThat(personalInfo.nickName()).isEqualTo(userInfo.getNickName());
        assertThat(personalInfo.profileImage()).isEqualTo(userInfo.getProfileImage());
        assertThat(personalInfo.averageGrade()).isEqualTo(4.3); // TODO 리뷰 기능 구현 필요
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
        UserPersonalInfoResponse personalInfo = userQueryUseCase.getPersonalInfo(userId);

        // then
        assertThat(personalInfo.profileImage()).isNull();
        List<UserPrTagEntity> prTags = userPrTagRepository.findByUserInfoId(userId);
        assertThat(prTags).isEmpty();
    }

    private Long getUserId(UserInfoEntity userInfo) {
        UserInfoEntity userInfoEntity = userRepository.save(userInfo);
        return userInfoEntity.getId();
    }

    private static Stream<Arguments> getUserEntity() {
        UserInfoEntity userInfoEntity =
                UserInfoEntity.builder()
                        .email("aa@aa.com")
                        .password("password")
                        .nickName("nickName")
                        .profileImage("브이하는내사진.jpg")
                        .providerType(ProviderType.LOCAL)
                        .build();
        return Stream.of(Arguments.of(userInfoEntity));
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

    // TODO 회원 리뷰 Entity 매개변수

}
