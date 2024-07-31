package com.boardgo.domain.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.boardgo.domain.mapper.UserInfoMapper;
import com.boardgo.domain.user.controller.dto.SignupRequest;
import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCommandServiceV1 implements UserUseCase {

	private final UserRepository userRepository;
	private final UserInfoMapper userInfoMapper;
	private final PasswordEncoder passwordEncoder;

	@Override
	public Long signup(SignupRequest signupRequest) {
		UserInfoEntity userInfo = userInfoMapper.from(signupRequest);
		log.debug(userInfo.getPassword());
		userInfo.encodePassword(passwordEncoder);
		UserInfoEntity savedUser = userRepository.save(userInfo);
		return savedUser.getId();
	}
}
