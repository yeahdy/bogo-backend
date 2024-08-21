package com.boardgo.integration.user.service;

import static com.boardgo.integration.fixture.UserInfoFixture.localUserInfoEntity;
import static com.boardgo.integration.fixture.UserInfoFixture.socialUserInfoEntity;
import static com.boardgo.integration.fixture.UserPrTagFixture.userPrTagEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.boardgo.common.exception.CustomNullPointException;
import com.boardgo.domain.user.controller.dto.SignupRequest;
import com.boardgo.domain.user.controller.dto.SocialSignupRequest;
import com.boardgo.domain.user.controller.dto.UserPersonalInfoUpdateRequest;
import com.boardgo.domain.user.entity.ProviderType;
import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.domain.user.entity.UserPrTagEntity;
import com.boardgo.domain.user.repository.UserPrTagRepository;
import com.boardgo.domain.user.repository.UserRepository;
import com.boardgo.domain.user.service.UserCommandUseCase;
import com.boardgo.integration.support.IntegrationTestSupport;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

public class UserCommandServiceV1Test extends IntegrationTestSupport {

    @Autowired private UserCommandUseCase userCommandUseCase;
    @Autowired private UserRepository userRepository;
    @Autowired private UserPrTagRepository userPrTagRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("사용자는 회원가입해서 userInfo 데이터를 생성할 수 있다")
    void 사용자는_회원가입해서_userInfo_데이터를_생성할_수_있다() {
        // given
        SignupRequest signupRequest =
                new SignupRequest("aa@aa.aa", "nickname", "password", List.of("prTag1", "prTag2"));
        // when
        Long signupUserId = userCommandUseCase.signup(signupRequest);
        // then
        List<UserPrTagEntity> userPrTagEntities =
                userPrTagRepository.findByUserInfoId(signupUserId);
        UserInfoEntity userInfoEntity = userRepository.findById(signupUserId).get();

        assertThat(userInfoEntity.getEmail()).isEqualTo(signupRequest.email());
        assertThat(userInfoEntity.getNickName()).isEqualTo(signupRequest.nickName());
        assertThat(userPrTagEntities.get(0).getTagName()).isEqualTo(signupRequest.prTags().get(0));
        assertThat(userPrTagEntities.get(1).getTagName()).isEqualTo(signupRequest.prTags().get(1));
    }

    @Test
    @DisplayName("사용자는 PrTag가 없어도 userInfo 데이터를 생성할 수 있다")
    void 사용자는_PrTag가_없어도_userInfo_데이터를_생성할_수_있다() {
        // given
        SignupRequest signupRequest = new SignupRequest("aa@aa.aa", "nickname", "password", null);
        // when
        Long signupUserId = userCommandUseCase.signup(signupRequest);
        // then
        UserInfoEntity userInfoEntity = userRepository.findById(signupUserId).get();

        assertThat(userInfoEntity.getEmail()).isEqualTo(signupRequest.email());
        assertThat(userInfoEntity.getNickName()).isEqualTo(signupRequest.nickName());
    }

    @ParameterizedTest
    @DisplayName("소셜 회원가입은 회원이 존재하지 않을 경우 예외를 발생한다")
    @MethodSource("getSocialSignupRequest")
    void 소셜_회원가입은_회원이_존재하지_않을_경우_예외를_발생한다(SocialSignupRequest request) {
        // given
        Long userId = 5626262352L;
        // when
        // then
        assertThatThrownBy(() -> userCommandUseCase.socialSignup(request, userId))
                .isInstanceOf(CustomNullPointException.class);
    }

    @ParameterizedTest
    @DisplayName("소셜 회원가입은 닉네임과 PR태그를 저장한다")
    @MethodSource("getSocialSignupRequest")
    void 소셜_회원가입은_닉네임과_PR태그를_저장한다(SocialSignupRequest request) {
        // given
        UserInfoEntity userInfoEntity =
                userRepository.save(socialUserInfoEntity(ProviderType.GOOGLE));

        // when
        userCommandUseCase.socialSignup(request, userInfoEntity.getId());

        // then
        UserInfoEntity selectedUserInfo = userRepository.findById(userInfoEntity.getId()).get();
        assertThat(selectedUserInfo.getNickName()).isEqualTo(request.nickName());

        List<UserPrTagEntity> prTagEntityList =
                userPrTagRepository.findByUserInfoId(userInfoEntity.getId());
        assertThat(prTagEntityList.size()).isPositive();

        List<String> prTags =
                prTagEntityList.stream()
                        .map(UserPrTagEntity::getTagName)
                        .collect(Collectors.toList());
        assertThat(prTags).isEqualTo(request.prTags());
    }

    private static Stream<Arguments> getSocialSignupRequest() {
        return Stream.of(
                Arguments.of(new SocialSignupRequest("Bread", List.of("ENFJ", "HAPPY", "SLEEP"))));
    }

    @Test
    @DisplayName("개인정보에서 닉네임과 비밀번호를 변경할 수 있다")
    void 개인정보에서_닉네임과_비밀번호를_변경할_수_있다() {
        // given
        UserInfoEntity originalUserInfo = userRepository.save(localUserInfoEntity());
        originalUserInfo.encodePassword(passwordEncoder);
        String originalPw = originalUserInfo.getPassword();
        UserPersonalInfoUpdateRequest updateRequest =
                new UserPersonalInfoUpdateRequest("juice", "626gsd가나@#");

        // when
        userCommandUseCase.updatePersonalInfo(originalUserInfo.getId(), updateRequest);

        // then
        UserInfoEntity changedUserInfo = userRepository.findById(originalUserInfo.getId()).get();
        assertThat(changedUserInfo.getNickName()).isEqualTo(updateRequest.nickName());
        assertThat(changedUserInfo.getPassword()).isNotEqualTo(originalPw);
    }

    @Test
    @DisplayName("전달된 프로필 이미지가 없을 경우 기존의 이미지는 삭제된다")
    void 전달된_프로필_이미지가_없을_경우_기존의_이미지는_삭제된다() {
        // given
        UserInfoEntity userInfo = userRepository.save(localUserInfoEntity());
        MultipartFile profileImage = null;

        // when
        userCommandUseCase.updateProfileImage(userInfo.getId(), profileImage);

        // then
        assertThat(userInfo.getProfileImage()).isEmpty();
    }

    @Test
    @DisplayName("전달된 PR태그가 없을 경우 모두 삭제된다")
    void 전달된_PR태그가_없을_경우_모두_삭제된다() {
        // given
        UserInfoEntity userInfo = userRepository.save(localUserInfoEntity());
        userPrTagRepository.save(userPrTagEntity(userInfo.getId(), "123"));
        userPrTagRepository.save(userPrTagEntity(userInfo.getId(), "456"));

        // when
        userCommandUseCase.updatePrTags(null, userInfo.getId());

        // then
        List<UserPrTagEntity> trTags = userPrTagRepository.findByUserInfoId(userInfo.getId());
        assertThat(trTags).isEmpty();
    }
}
