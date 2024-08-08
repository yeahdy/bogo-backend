package com.boardgo.integration.user.service;

import com.boardgo.common.exception.CustomIllegalArgumentException;
import com.boardgo.domain.user.controller.dto.EmailRequest;
import com.boardgo.domain.user.controller.dto.NickNameRequest;
import com.boardgo.domain.user.entity.ProviderType;
import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.domain.user.repository.UserRepository;
import com.boardgo.domain.user.service.UserQueryUseCase;
import com.boardgo.integration.support.IntegrationTestSupport;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class UserQueryServiceV1Test extends IntegrationTestSupport {
    @Autowired private UserRepository userRepository;
    @Autowired private UserQueryUseCase userQueryUseCase;

    @Test
    @DisplayName("해당 이메일이 존재하지 않으면 에러가 발생하지 않는다")
    void 해당_이메일이_존재하지_않으면_아무일도_일어나지_않는다() {
        // given
        EmailRequest emailRequest = new EmailRequest("aa@aa.com");
        // when
        // then
        userQueryUseCase.existEmail(emailRequest);
    }

    @Test
    @DisplayName("해당 이메일이 존재하면 error를 반환한다")
    void 해당_이메일이_존재하면_error를_반환한다() {
        // given
        EmailRequest emailRequest = new EmailRequest("aa@aa.com");
        UserInfoEntity userInfoEntity =
                UserInfoEntity.builder()
                        .email("aa@aa.com")
                        .password("password")
                        .nickName("nickName")
                        .providerType(ProviderType.LOCAL)
                        .build();
        userRepository.save(userInfoEntity);
        // when

        // then
        Assertions.assertThatThrownBy(() -> userQueryUseCase.existEmail(emailRequest))
                .isInstanceOf(CustomIllegalArgumentException.class);
    }

    @Test
    @DisplayName("해당 닉네임이 존재하지 않으면 에러가 발생하지 않는다")
    void 해당_닉네임이_존재하지_않으면_에러가_발생하지_않는다() {
        // given
        NickNameRequest nickNameRequest = new NickNameRequest("nickName");
        // when
        userQueryUseCase.existNickName(nickNameRequest);
        // then

    }

    @Test
    @DisplayName("해당 닉네임이 존재하면 에러가 발생한다")
    void 해당_닉네임이_존재하면_에러가_발생한다() {
        // given
        NickNameRequest nickNameRequest = new NickNameRequest("nickName");
        UserInfoEntity userInfoEntity =
                UserInfoEntity.builder()
                        .email("aa@aa.com")
                        .password("password")
                        .nickName("nickName")
                        .providerType(ProviderType.LOCAL)
                        .build();
        userRepository.save(userInfoEntity);
        // when
        // then
        Assertions.assertThatThrownBy(() -> userQueryUseCase.existNickName(nickNameRequest))
                .isInstanceOf(CustomIllegalArgumentException.class);
    }
}
