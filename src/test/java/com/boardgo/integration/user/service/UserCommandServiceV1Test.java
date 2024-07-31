package com.boardgo.integration.user.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.boardgo.domain.user.controller.dto.SignupRequest;
import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.domain.user.repository.UserRepository;
import com.boardgo.domain.user.service.UserUseCase;
import com.boardgo.integration.support.IntegrationTestSupport;

public class UserCommandServiceV1Test extends IntegrationTestSupport {

	@Autowired
	private UserUseCase userUseCase;
	@Autowired
	private UserRepository userRepository;

	@Test
	@DisplayName("사용자는 회원가입해서 userInfo 데이터를 생성할 수 있다")
	void 사용자는_회원가입해서_userInfo_데이터를_생성할_수_있다() {
		//given
		SignupRequest signupRequest = new SignupRequest("aa@aa.aa", "nickname", "password");
		//when
		Long signupUserId = userUseCase.signup(signupRequest);
		//then
		UserInfoEntity userInfoEntity = userRepository.findById(signupUserId).get();

		assertThat(userInfoEntity.getEmail()).isEqualTo(signupRequest.email());
		assertThat(userInfoEntity.getNickname()).isEqualTo(signupRequest.nickName());
	}
}
