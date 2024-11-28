package com.boardgo.domain.termsconditions.service.facade;

import static com.boardgo.domain.termsconditions.entity.enums.TermsConditionsType.PUSH;

import com.boardgo.common.exception.CustomIllegalArgumentException;
import com.boardgo.domain.mapper.TermsConditionsMapper;
import com.boardgo.domain.notification.service.UserNotificationSettingCommandUseCase;
import com.boardgo.domain.notification.service.UserNotificationSettingQueryUseCase;
import com.boardgo.domain.notification.service.response.UserNotificationSettingResponse;
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
    private final UserNotificationSettingQueryUseCase userNotificationSettingQueryUseCase;
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

    // 푸시 설정이 변경 후 회원의 푸시 약관동의가 변경
    @Override
    public void updatePushTermsConditions(Long userId, Boolean isAgreed) {
        // 알림설정이 Y 이고, 회원의 기존 푸시 약관동의가 N 이라면 Y로 변경
        if (isAgreed) {
            userTermsConditionsCommandUseCase.updatePushTermsCondition(userId);
        } else {
            // 회원의 모든 알림설정이 N 이라면 푸시약관동의 N 변경
            List<UserNotificationSettingResponse> userNotificationSettings =
                    userNotificationSettingQueryUseCase.getUserNotificationSettingsList(userId);
            for (UserNotificationSettingResponse setting : userNotificationSettings) {
                if (setting.isAgreed()) { // 하나라도 true 가 존재하면 패스
                    return;
                }
            }
            userTermsConditionsCommandUseCase.updatePushTermsCondition(userId);
        }
    }
}
