package com.boardgo.integration.jwt.service;

import static org.assertj.core.api.Assertions.*;

import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.integration.data.UserInfoData;
import com.boardgo.integration.support.IntegrationTestSupport;
import com.boardgo.jwt.entity.AuthEntity;
import com.boardgo.jwt.repository.AuthRepository;
import com.boardgo.jwt.service.LoginService;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class LoginServiceTest extends IntegrationTestSupport {

    @Autowired private AuthRepository authRepository;
    @Autowired private LoginService loginService;
    @Autowired EntityManager entityManager;

    @Test
    @DisplayName("리프레시 토큰을 업데이트 할 수 있다")
    void 리프레시_토큰을_업데이트_할_수_있다() {
        // given
        UserInfoEntity userInfo = UserInfoData.userInfoEntityData("aa@aa.com", "nickname").build();
        AuthEntity auth =
                AuthEntity.builder()
                        .refreshToken("refreshToken")
                        .expirationDatetime(LocalDateTime.now().plusDays(1))
                        .build();
        AuthEntity savedAuth = authRepository.save(auth);
        // when
        String newRefreshToken = "newRefreshToken";
        loginService.updateTokenWithOutValidation(savedAuth.getId(), newRefreshToken);
        // then
        entityManager.clear();
        AuthEntity authEntity = authRepository.findById(savedAuth.getId()).get();
        assertThat(authEntity.getRefreshToken()).isEqualTo(newRefreshToken);
    }
}
