package com.boardgo.domain.user.service;

import static com.boardgo.common.exception.advice.dto.ErrorCode.DUPLICATE_DATA;

import com.boardgo.common.exception.CustomIllegalArgumentException;
import com.boardgo.domain.mapper.UserInfoMapper;
import com.boardgo.domain.user.controller.request.EmailRequest;
import com.boardgo.domain.user.controller.request.NickNameRequest;
import com.boardgo.domain.user.entity.enums.ProviderType;
import com.boardgo.domain.user.repository.UserRepository;
import com.boardgo.domain.user.repository.projection.PersonalInfoProjection;
import com.boardgo.domain.user.service.response.UserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserQueryServiceV1 implements UserQueryUseCase {
    private final UserRepository userRepository;
    private final UserInfoMapper userInfoMapper;

    @Override
    public void existEmail(EmailRequest emailRequest) {
        if (userRepository.existsByEmailAndProviderType(emailRequest.email(), ProviderType.LOCAL)) {
            throw new CustomIllegalArgumentException(DUPLICATE_DATA.getCode(), "중복된 Email입니다.");
        }
    }

    @Override
    public void existNickName(NickNameRequest nickNameRequest) {
        if (userRepository.existsByNickName(nickNameRequest.nickName())) {
            throw new CustomIllegalArgumentException(DUPLICATE_DATA.getCode(), "중복된 닉네임입니다.");
        }
    }

    @Override
    public UserInfoResponse getPersonalInfo(Long userId) {
        PersonalInfoProjection personalInfoProjection = userRepository.findByUserInfoId(userId);
        return userInfoMapper.toUserInfoResponse(personalInfoProjection);
    }
}
