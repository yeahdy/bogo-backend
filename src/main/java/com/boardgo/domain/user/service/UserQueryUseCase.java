package com.boardgo.domain.user.service;

import com.boardgo.domain.user.controller.request.EmailRequest;
import com.boardgo.domain.user.controller.request.NickNameRequest;
import com.boardgo.domain.user.service.response.UserInfoResponse;

public interface UserQueryUseCase {
    void existEmail(EmailRequest emailRequest);

    void existNickName(NickNameRequest nickNameRequest);

    UserInfoResponse getPersonalInfo(Long userId);
}
