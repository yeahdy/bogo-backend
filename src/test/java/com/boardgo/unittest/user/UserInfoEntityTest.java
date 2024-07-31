package com.boardgo.unittest.user;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.boardgo.domain.mapper.UserInfoMapper;
import com.boardgo.domain.user.controller.dto.SignupRequest;
import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.unittest.user.fake.FakePasswordEncoder;

public class UserInfoEntityTest {
	@Test
	@DisplayName("userInfoEntity는 패스워드를 인코딩할 수 있다")
	void userInfoEntity는_패스워드를_인코딩할_수_있다() {
		//given
		String encodedPassword = "12345678";
		FakePasswordEncoder fakePasswordEncoder = new FakePasswordEncoder();
		SignupRequest signupRequest = new SignupRequest("aa@aa.aa", "nickname", "password");
		UserInfoMapper instance = UserInfoMapper.INSTANCE;

		//when
		UserInfoEntity userInfo = instance.from(signupRequest);
		userInfo.encodePassword(fakePasswordEncoder);
		//then
		assertThat(userInfo.getPassword()).isEqualTo(encodedPassword);
	}
}
