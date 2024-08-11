package com.boardgo.unittest.user;

import static org.assertj.core.api.Assertions.assertThat;

import com.boardgo.domain.mapper.UserInfoMapper;
import com.boardgo.domain.user.controller.dto.SignupRequest;
import com.boardgo.domain.user.controller.dto.SocialSignupRequest;
import com.boardgo.domain.user.entity.ProviderType;
import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.unittest.user.fake.FakePasswordEncoder;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class UserInfoEntityTest {
    @Test
    @DisplayName("userInfoEntity는 패스워드를 인코딩할 수 있다")
    void userInfoEntity는_패스워드를_인코딩할_수_있다() {
        // given
        String encodedPassword = "12345678";
        FakePasswordEncoder fakePasswordEncoder = new FakePasswordEncoder();
        SignupRequest signupRequest = new SignupRequest("aa@aa.aa", "nickname", "password", null);
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
        SignupRequest signupRequest = new SignupRequest("aa@aa.aa", "nickname", "password", null);
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
        SocialSignupRequest request = new SocialSignupRequest("changeName", List.of("Happy"));
        UserInfoEntity userInfoEntity =
                UserInfoEntity.builder()
                        .id(1L)
                        .email("1355245636623452")
                        .password(null)
                        .nickName(null)
                        .providerType(ProviderType.GOOGLE)
                        .deleteAt(null)
                        .build();
        // when
        userInfoEntity.updateNickname(request.nickName());
        // then
        assertThat(userInfoEntity.getNickName()).isEqualTo(request.nickName());
        assertThat(userInfoEntity.getNickName()).isNotBlank();
    }
}
