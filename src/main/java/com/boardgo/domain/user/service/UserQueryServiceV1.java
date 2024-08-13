package com.boardgo.domain.user.service;

import static com.boardgo.common.exception.advice.dto.ErrorCode.DUPLICATE_DATA;

import com.boardgo.common.exception.CustomIllegalArgumentException;
import com.boardgo.common.exception.CustomNullPointException;
import com.boardgo.domain.mapper.UserInfoMapper;
import com.boardgo.domain.user.controller.dto.EmailRequest;
import com.boardgo.domain.user.controller.dto.NickNameRequest;
import com.boardgo.domain.user.controller.dto.UserPersonalInfoResponse;
import com.boardgo.domain.user.entity.ProviderType;
import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.domain.user.entity.UserPrTagEntity;
import com.boardgo.domain.user.repository.UserPrTagRepository;
import com.boardgo.domain.user.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserQueryServiceV1 implements UserQueryUseCase {
    private final UserRepository userRepository;
    private final UserPrTagRepository userPrTagRepository;
    private final UserInfoMapper UserInfoMapper;

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
    public UserPersonalInfoResponse getPersonalInfo(Long userId) {
        UserInfoEntity userInfoEntity =
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new CustomNullPointException("회원이 존재하지 않습니다"));
        // TODO. 리뷰 기능 구현 필요: 평균별점
        // TODO. 이미지 조회 시 s3 url 붙여서 나가는지?
        Double averageGrade = 4.3;
        List<UserPrTagEntity> userPrTagEntities =
                userPrTagRepository.findByUserInfoId(userInfoEntity.getId());
        List<String> prTagList =
                userPrTagEntities.stream()
                        .map(UserPrTagEntity::getTagName)
                        .collect(Collectors.toList());
        return UserInfoMapper.toUserPersonalInfoResponse(userInfoEntity, averageGrade, prTagList);
    }
}
