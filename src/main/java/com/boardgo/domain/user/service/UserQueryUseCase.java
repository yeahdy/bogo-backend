package com.boardgo.domain.user.service;

import com.boardgo.domain.user.controller.dto.EmailRequest;
import com.boardgo.domain.user.controller.dto.NickNameRequest;
import com.boardgo.domain.user.controller.dto.OtherPersonalInfoResponse;
import com.boardgo.domain.user.controller.dto.UserPersonalInfoResponse;

public interface UserQueryUseCase {
    void existEmail(EmailRequest emailRequest);

    void existNickName(NickNameRequest nickNameRequest);

    UserPersonalInfoResponse getPersonalInfo(Long userId);

    OtherPersonalInfoResponse getOtherPersonalInfo(Long userId);
}
