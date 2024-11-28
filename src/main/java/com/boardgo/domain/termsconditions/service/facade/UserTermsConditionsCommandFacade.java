package com.boardgo.domain.termsconditions.service.facade;

import com.boardgo.domain.termsconditions.controller.request.TermsConditionsCreateRequest;
import java.util.List;

public interface UserTermsConditionsCommandFacade {
    void createUserTermsConditions(
            List<TermsConditionsCreateRequest> termsConditionsCreateRequest, Long userId);

    void updatePushTermsConditions(Long userId, Boolean isAgreed);
}
