package com.boardgo.domain.user.service;

import static com.boardgo.common.exception.advice.dto.ErrorCode.DUPLICATE_DATA;

import com.boardgo.common.exception.CustomIllegalArgumentException;
import com.boardgo.common.exception.CustomNullPointException;
import com.boardgo.domain.mapper.UserInfoMapper;
import com.boardgo.domain.meeting.service.response.UserParticipantResponse;
import com.boardgo.domain.user.controller.request.EmailRequest;
import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.domain.user.entity.enums.ProviderType;
import com.boardgo.domain.user.repository.UserRepository;
import com.boardgo.domain.user.repository.projection.PersonalInfoProjection;
import com.boardgo.domain.user.service.response.UserInfoResponse;
import java.util.List;
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
    public UserInfoEntity getUserInfoEntity(Long id) {
        return userRepository
                .findById(id)
                .orElseThrow(() -> new CustomNullPointException("회원이 존재하지 않습니다"));
    }

    @Override
    public void existEmail(EmailRequest emailRequest) {
        if (userRepository.existsByEmailAndProviderType(emailRequest.email(), ProviderType.LOCAL)) {
            throw new CustomIllegalArgumentException(DUPLICATE_DATA.getCode(), "중복된 Email입니다.");
        }
    }

    @Override
    public boolean existNickName(String nickName) {
        if (userRepository.existsByNickName(nickName)) {
            throw new CustomIllegalArgumentException(DUPLICATE_DATA.getCode(), "중복된 닉네임입니다.");
        }
        return false;
    }

    @Override
    public UserInfoResponse getPersonalInfo(Long userId) {
        PersonalInfoProjection personalInfoProjection = userRepository.findByUserInfoId(userId);
        return userInfoMapper.toUserInfoResponse(personalInfoProjection);
    }

    @Override
    public List<UserParticipantResponse> findByMeetingId(Long meetingId) {
        return userRepository.findByMeetingId(meetingId);
    }

    @Override
    public boolean existById(Long id) {
        return userRepository.existsById(id);
    }
}
