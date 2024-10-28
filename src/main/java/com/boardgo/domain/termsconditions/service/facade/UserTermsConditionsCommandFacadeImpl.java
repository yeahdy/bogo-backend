package com.boardgo.domain.termsconditions.service.facade;

import static com.boardgo.domain.termsconditions.entity.enums.TermsConditionsType.PUSH;

import com.boardgo.common.exception.CustomIllegalArgumentException;
import com.boardgo.domain.mapper.TermsConditionsMapper;
import com.boardgo.domain.notification.service.UserNotificationSettingCommandUseCase;
import com.boardgo.domain.termsconditions.controller.request.TermsConditionsCreateRequest;
import com.boardgo.domain.termsconditions.entity.TermsConditionsEntity;
import com.boardgo.domain.termsconditions.entity.UserTermsConditionsEntity;
import com.boardgo.domain.termsconditions.service.TermsConditionsFactory;
import com.boardgo.domain.termsconditions.service.UserTermsConditionsCommandUseCase;
import com.boardgo.domain.termsconditions.service.UserTermsConditionsQueryUseCase;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Transactional
public class UserTermsConditionsCommandFacadeImpl implements UserTermsConditionsCommandFacade {
    private final UserTermsConditionsCommandUseCase userTermsConditionsCommandUseCase;
    private final UserTermsConditionsQueryUseCase userTermsConditionsQueryUseCase;
    private final UserNotificationSettingCommandUseCase userNotificationSettingCommandUseCase;
    private final TermsConditionsMapper termsConditionsMapper;

    @Override
    public void createUserTermsConditions(
            List<TermsConditionsCreateRequest> termsConditionsCreateRequest, Long userId) {
        validateUserTermsConditions(
                TermsConditionsFactory.size(), termsConditionsCreateRequest.size(), userId);

        List<UserTermsConditionsEntity> userTermsConditionsEntities = new ArrayList<>();
        termsConditionsCreateRequest.forEach(
                termsConditions -> {
                    TermsConditionsEntity termsConditionsEntity =
                            TermsConditionsFactory.get(termsConditions.termsConditionsType());
                    if (!termsConditionsEntity.isRequired(termsConditions.agreement())) {
                        throw new CustomIllegalArgumentException("필수 약관은 모두 동의되어야 합니다");
                    }
                    // 푸시 알림설정 저장
                    if (PUSH.equals(termsConditionsEntity.getType())) {
                        userNotificationSettingCommandUseCase.create(
                                userId, termsConditions.agreement());
                    }

                    userTermsConditionsEntities.add(
                            termsConditionsMapper.toUserTermsConditionsEntity(
                                    termsConditionsEntity, termsConditions.agreement(), userId));
                });
        userTermsConditionsCommandUseCase.createAll(userTermsConditionsEntities);
    }

    private void validateUserTermsConditions(
            int termsConditionsSize, int requestSize, Long userId) {
        if (termsConditionsSize != requestSize) {
            throw new CustomIllegalArgumentException("약관동의 항목의 갯수가 일치하지 않습니다");
        }
        if (userTermsConditionsQueryUseCase.existsUser(userId)) {
            throw new CustomIllegalArgumentException("이미 약관동의 완료된 회원입니다");
        }
    }
}
