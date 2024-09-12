package com.boardgo.domain.user.service;

import com.boardgo.domain.meeting.service.response.UserParticipantResponse;
import com.boardgo.domain.user.controller.request.EmailRequest;
import com.boardgo.domain.user.controller.request.NickNameRequest;
import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.domain.user.service.response.UserInfoResponse;
import java.util.List;

public interface UserQueryUseCase {

    UserInfoEntity getById(Long id);

    void existEmail(EmailRequest emailRequest);

    void existNickName(NickNameRequest nickNameRequest);

    UserInfoResponse getPersonalInfo(Long userId);

    List<UserParticipantResponse> findByMeetingId(Long meetingId);
}
