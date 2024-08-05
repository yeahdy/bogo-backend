package com.boardgo.integration.user.service;

import static org.assertj.core.api.Assertions.*;

import com.boardgo.domain.user.controller.dto.SignupRequest;
import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.domain.user.entity.UserPrTagEntity;
import com.boardgo.domain.user.repository.UserPrTagRepository;
import com.boardgo.domain.user.repository.UserRepository;
import com.boardgo.domain.user.service.UserUseCase;
import com.boardgo.integration.support.IntegrationTestSupport;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class UserCommandServiceV1Test extends IntegrationTestSupport {

    @Autowired private UserUseCase userUseCase;
    @Autowired private UserRepository userRepository;
    @Autowired private UserPrTagRepository userPrTagRepository;

    @Test
    @DisplayName("사용자는 회원가입해서 userInfo 데이터를 생성할 수 있다")
    void 사용자는_회원가입해서_userInfo_데이터를_생성할_수_있다() {
        // given
        SignupRequest signupRequest =
                new SignupRequest("aa@aa.aa", "nickname", "password", List.of("prTag1", "prTag2"));
        // when
        Long signupUserId = userUseCase.signup(signupRequest);
        // then
        List<UserPrTagEntity> userPrTagEntities =
                userPrTagRepository.findByUserInfoId(signupUserId);
        UserInfoEntity userInfoEntity = userRepository.findById(signupUserId).get();

        assertThat(userInfoEntity.getEmail()).isEqualTo(signupRequest.email());
        assertThat(userInfoEntity.getNickName()).isEqualTo(signupRequest.nickName());
        assertThat(userPrTagEntities.get(0).getTagName()).isEqualTo(signupRequest.prTags().get(0));
        assertThat(userPrTagEntities.get(1).getTagName()).isEqualTo(signupRequest.prTags().get(1));
    }

    @Test
    @DisplayName("사용자는 PrTag가 없어도 userInfo 데이터를 생성할 수 있다")
    void 사용자는_PrTag가_없어도_userInfo_데이터를_생성할_수_있다() {
        // given
        SignupRequest signupRequest = new SignupRequest("aa@aa.aa", "nickname", "password", null);
        // when
        Long signupUserId = userUseCase.signup(signupRequest);
        // then
        UserInfoEntity userInfoEntity = userRepository.findById(signupUserId).get();

        assertThat(userInfoEntity.getEmail()).isEqualTo(signupRequest.email());
        assertThat(userInfoEntity.getNickName()).isEqualTo(signupRequest.nickName());
    }
}
