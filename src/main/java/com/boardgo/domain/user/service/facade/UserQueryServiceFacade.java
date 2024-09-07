package com.boardgo.domain.user.service.facade;

import com.boardgo.domain.user.service.response.OtherPersonalInfoResponse;
import com.boardgo.domain.user.service.response.UserPersonalInfoResponse;

public interface UserQueryServiceFacade {
    OtherPersonalInfoResponse getOtherPersonalInfo(Long userId);

    UserPersonalInfoResponse getPersonalInfo(Long userId);
}
