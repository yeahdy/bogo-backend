package com.boardgo.domain.user.service;

import com.boardgo.domain.user.controller.request.EmailRequest;
import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.domain.user.service.response.UserInfoResponse;

public interface UserQueryUseCase {

    UserInfoEntity getUserInfoEntity(Long id);

    void existEmail(EmailRequest emailRequest);

    boolean existNickName(String nickName);

    UserInfoResponse getPersonalInfo(Long userId);

    boolean existById(Long id);
}
