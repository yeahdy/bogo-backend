package com.boardgo.unittest.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.boardgo.domain.mapper.UserInfoMapper;
import com.boardgo.domain.user.controller.request.SignupRequest;
import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.domain.user.entity.enums.ProviderType;
import com.boardgo.unittest.user.fake.FakePasswordEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class UserInfoEntityTest {
    @Test
    @DisplayName("userInfoEntity는 패스워드를 인코딩할 수 있다")
    void userInfoEntity는_패스워드를_인코딩할_수_있다() {
        // given
        String encodedPassword = "12345678";
        FakePasswordEncoder fakePasswordEncoder = new FakePasswordEncoder();
        SignupRequest signupRequest =
                new SignupRequest("aa@aa.aa", "nickname", "password", null, null);
        UserInfoMapper instance = UserInfoMapper.INSTANCE;

        // when
        UserInfoEntity userInfo = instance.toUserInfoEntity(signupRequest);
        userInfo.encodePassword(fakePasswordEncoder);

        // then
        assertThat(userInfo.getPassword()).isEqualTo(encodedPassword);
    }

    @Test
    @DisplayName("signupRequest는 userInfoEntity로 매핑할 수 있다")
    void signupRequest는_userInfoEntity로_매핑할_수_있다() {
        // given
        SignupRequest signupRequest =
                new SignupRequest("aa@aa.aa", "nickname", "password", null, null);
        UserInfoMapper instance = UserInfoMapper.INSTANCE;
        // when
        UserInfoEntity userInfo = instance.toUserInfoEntity(signupRequest);
        // then
        assertThat(signupRequest.email()).isEqualTo(userInfo.getEmail());
        assertThat(signupRequest.nickName()).isEqualTo(userInfo.getNickName());
        assertThat(signupRequest.password()).isEqualTo(userInfo.getPassword());
    }

    @Test
    @DisplayName("userInfoEntity는 닉네임을 변경할 수 있다")
    void userInfoEntity는_닉네임을_변경할_수_있다() {
        // given
        String nickName = "changeName";
        UserInfoEntity userInfoEntity =
                UserInfoEntity.builder()
                        .id(1L)
                        .email("1355245636623452")
                        .providerType(ProviderType.GOOGLE)
                        .build();
        // when
        userInfoEntity.updateNickname(nickName);
        // then
        assertThat(userInfoEntity.getNickName()).isEqualTo(nickName);
        assertThat(userInfoEntity.getNickName()).isNotBlank();
    }

    @Test
    @DisplayName("프로필 이미지 변경 시 새로운 이미지로 변경된다")
    void 프로필_이미지_변경_시_새로운_이미지로_변경된다() {
        // given
        UserInfoEntity userInfoEntity =
                UserInfoEntity.builder()
                        .id(1L)
                        .email("1355245636623452")
                        .nickName("루피에오")
                        .providerType(ProviderType.GOOGLE)
                        .profileImage("루피.jpg")
                        .build();
        String newImage = "뽀로로.jpg";

        // when
        userInfoEntity.updateProfileImage(newImage);

        // then
        assertThat(userInfoEntity.getProfileImage()).isEqualTo(newImage);
    }

    @Test
    @DisplayName("회원 비밀번호 변경 시 새로운 비밀번호로 변경된다")
    void 회원_비밀번호_변경_시_새로운_비밀번호로_변경된다() {
        // given
        UserInfoEntity userInfoEntity =
                UserInfoEntity.builder()
                        .id(1L)
                        .email("fhsdhasl@naver.com")
                        .password("fhdslg342!@#")
                        .nickName("크롱")
                        .providerType(ProviderType.LOCAL)
                        .build();
        String newPassword = "12345678";
        FakePasswordEncoder fakePasswordEncoder = new FakePasswordEncoder();

        // when
        userInfoEntity.updatePassword(newPassword, fakePasswordEncoder);

        // then
        assertThat(userInfoEntity.getPassword()).isEqualTo(newPassword);
    }
}
