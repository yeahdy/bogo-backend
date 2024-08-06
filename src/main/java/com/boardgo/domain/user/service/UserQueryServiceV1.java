package com.boardgo.domain.user.service;

import static com.boardgo.common.exception.advice.dto.ErrorCode.*;

import com.boardgo.common.exception.CustomIllegalArgumentException;
import com.boardgo.domain.oauth2.entity.ProviderType;
import com.boardgo.domain.user.controller.dto.EmailRequest;
import com.boardgo.domain.user.controller.dto.NickNameRequest;
import com.boardgo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserQueryServiceV1 implements UserQueryUseCase {
    private final UserRepository userRepository;

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
}
