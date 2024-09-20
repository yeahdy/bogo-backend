package com.boardgo.domain.user.service.facade;

import static com.boardgo.common.utils.ValidateUtils.validateNickname;
import static com.boardgo.common.utils.ValidateUtils.validatePrTag;

import com.boardgo.common.exception.DuplicateException;
import com.boardgo.domain.termsconditions.service.facade.UserTermsConditionsCommandFacade;
import com.boardgo.domain.user.controller.request.SignupRequest;
import com.boardgo.domain.user.controller.request.SocialSignupRequest;
import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.domain.user.service.UserCommandUseCase;
import com.boardgo.domain.user.service.UserPrTagCommandUseCase;
import com.boardgo.domain.user.service.UserQueryUseCase;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Transactional
public class UserCommandFacadeImpl implements UserCommandFacade {

    private final UserCommandUseCase userCommandUseCase;
    private final UserQueryUseCase userQueryUseCase;
    private final UserPrTagCommandUseCase userPrTagCommandUseCase;
    private final UserTermsConditionsCommandFacade userTermsConditionsCommandFacade;

    @Override
    public Long signup(SignupRequest signupRequest) {
        validateNickNameAndPrTag(signupRequest.nickName(), signupRequest.prTags());
        Long userId = userCommandUseCase.save(signupRequest);
        userTermsConditionsCommandFacade.createUserTermsConditions(
                signupRequest.termsConditions(), userId);
        userPrTagCommandUseCase.bulkInsertPrTags(signupRequest.prTags(), userId);
        return userId;
    }

    @Override
    public Long socialSignup(SocialSignupRequest signupRequest, Long userId) {
        UserInfoEntity userInfoEntity = userQueryUseCase.getUserInfoEntity(userId);
        if (userQueryUseCase.existNickName(signupRequest.nickName())) {
            throw new DuplicateException("중복된 닉네임입니다.");
        }
        validateNickNameAndPrTag(signupRequest.nickName(), signupRequest.prTags());
        userTermsConditionsCommandFacade.createUserTermsConditions(
                signupRequest.termsConditions(), userId);
        userInfoEntity.updateNickname(signupRequest.nickName());
        userPrTagCommandUseCase.bulkInsertPrTags(signupRequest.prTags(), userInfoEntity.getId());
        return userInfoEntity.getId();
    }

    private void validateNickNameAndPrTag(String nickName, List<String> prTags) {
        validateNickname(nickName);
        if (!Objects.isNull(prTags)) {
            validatePrTag(prTags);
        }
    }
}
