package com.boardgo.domain.user.service;

import com.boardgo.domain.user.controller.dto.EmailRequest;
import com.boardgo.domain.user.controller.dto.NickNameRequest;

public interface UserQueryUseCase {
    void existEmail(EmailRequest emailRequest);

    void existNickName(NickNameRequest nickNameRequest);
}
