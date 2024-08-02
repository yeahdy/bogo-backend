package com.boardgo.domain.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.boardgo.domain.mapper.UserInfoMapper;
import com.boardgo.domain.user.controller.dto.SignupRequest;
import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.domain.user.repository.UserPrTagRepository;
import com.boardgo.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserCommandServiceV1 implements UserUseCase {
	private final UserRepository userRepository;
	private final UserPrTagRepository userPrTagRepository;
	private final UserInfoMapper userInfoMapper;
	private final PasswordEncoder passwordEncoder;

	@Override
	public Long signup(SignupRequest signupRequest) {
		UserInfoEntity userInfo = userInfoMapper.toUserInfoEntity(signupRequest);
		userInfo.encodePassword(passwordEncoder);
		UserInfoEntity savedUser = userRepository.save(userInfo);
		userPrTagRepository.bulkInsertPrTags(signupRequest.prTags(), savedUser.getId());
		return savedUser.getId();
	}
}
