package com.boardgo.domain.user.service.facade;

import com.boardgo.domain.mapper.UserInfoMapper;
import com.boardgo.domain.meeting.service.MeetingParticipantQueryUseCase;
import com.boardgo.domain.review.service.ReviewQueryUseCase;
import com.boardgo.domain.review.service.response.MyEvaluationTagsResponse;
import com.boardgo.domain.user.service.UserQueryUseCase;
import com.boardgo.domain.user.service.response.OtherPersonalInfoResponse;
import com.boardgo.domain.user.service.response.UserInfoResponse;
import com.boardgo.domain.user.service.response.UserPersonalInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class UserQueryServiceFacadeImpl implements UserQueryServiceFacade {

    private final UserQueryUseCase userQueryUseCase;
    private final ReviewQueryUseCase reviewQueryUseCase;
    private final MeetingParticipantQueryUseCase meetingParticipantQueryUseCase;
    private final UserInfoMapper userInfoMapper;

    public OtherPersonalInfoResponse getOtherPersonalInfo(Long userId) {
        int meetingCount = meetingParticipantQueryUseCase.getMeetingCount(userId);
        MyEvaluationTagsResponse myEvaluationTags = reviewQueryUseCase.getMyEvaluationTags(userId);
        return userInfoMapper.toUserPersonalInfoResponse(
                getPersonalInfo(userId), meetingCount, myEvaluationTags);
    }

    public UserPersonalInfoResponse getPersonalInfo(Long userId) {
        UserInfoResponse userPersonalInfo = userQueryUseCase.getPersonalInfo(userId);
        Double averageRating = reviewQueryUseCase.getAverageRating(userId);
        return userInfoMapper.toUserPersonalInfoResponse(userPersonalInfo, averageRating);
    }
}
