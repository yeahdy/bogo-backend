package com.boardgo.domain.user.service;

import static com.boardgo.common.exception.advice.dto.ErrorCode.DUPLICATE_DATA;
import static com.boardgo.domain.meeting.entity.enums.ParticipantType.LEADER;
import static com.boardgo.domain.meeting.entity.enums.ParticipantType.PARTICIPANT;

import com.boardgo.common.exception.CustomIllegalArgumentException;
import com.boardgo.domain.mapper.UserInfoMapper;
import com.boardgo.domain.meeting.repository.MeetingParticipantRepository;
import com.boardgo.domain.review.repository.ReviewRepository;
import com.boardgo.domain.user.controller.request.EmailRequest;
import com.boardgo.domain.user.controller.request.NickNameRequest;
import com.boardgo.domain.user.entity.enums.ProviderType;
import com.boardgo.domain.user.repository.UserPrTagRepository;
import com.boardgo.domain.user.repository.UserRepository;
import com.boardgo.domain.user.repository.projection.PersonalInfoProjection;
import com.boardgo.domain.user.service.response.OtherPersonalInfoResponse;
import com.boardgo.domain.user.service.response.UserPersonalInfoResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserQueryServiceV1 implements UserQueryUseCase {
    private final UserRepository userRepository;
    private final UserPrTagRepository userPrTagRepository;
    private final MeetingParticipantRepository meetingParticipantRepository;
    private final UserInfoMapper UserInfoMapper;
    private final ReviewRepository reviewRepository;

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
        PersonalInfoProjection personalInfoProjection = userRepository.findByUserInfoId(userId);
        Double averageRating = reviewRepository.findRatingAvgByRevieweeId(userId);
        averageRating = averageRating == null ? 0.0 : averageRating;
        return UserInfoMapper.toUserPersonalInfoResponse(personalInfoProjection, averageRating);
    }

    @Override
    public OtherPersonalInfoResponse getOtherPersonalInfo(Long userId) {
        int meetingCount =
                meetingParticipantRepository.countByTypeAndUserInfoId(
                        List.of(LEADER, PARTICIPANT), userId);
        return UserInfoMapper.toUserPersonalInfoResponse(getPersonalInfo(userId), meetingCount);
    }
}
