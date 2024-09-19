package com.boardgo.integration.user.service;

import static com.boardgo.integration.fixture.UserInfoFixture.localUserInfoEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.boardgo.common.exception.DuplicateException;
import com.boardgo.domain.user.controller.request.SignupRequest;
import com.boardgo.domain.user.controller.request.UserPersonalInfoUpdateRequest;
import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.domain.user.repository.UserRepository;
import com.boardgo.domain.user.service.UserCommandUseCase;
import com.boardgo.integration.support.IntegrationTestSupport;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

public class UserCommandServiceV1Test extends IntegrationTestSupport {

    @Autowired private UserCommandUseCase userCommandUseCase;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

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
    @DisplayName("닉네임이 중복일 경우 예외가 발생한다")
    void 닉네임이_중복일_경우_예외가_발생한다() {
        // given
        userCommandUseCase.save(
                new SignupRequest("aa@aa.aa", "nickname", "password", List.of("prTag1", "prTag2")));
        UserInfoEntity user = userRepository.save(localUserInfoEntity());
        UserPersonalInfoUpdateRequest updateRequest =
                new UserPersonalInfoUpdateRequest("nickname", "test12!@");

        // when
        // then
        assertThatThrownBy(() -> userCommandUseCase.updatePersonalInfo(user.getId(), updateRequest))
                .isInstanceOf(DuplicateException.class)
                .hasMessageContaining("중복된 닉네임");
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
}
