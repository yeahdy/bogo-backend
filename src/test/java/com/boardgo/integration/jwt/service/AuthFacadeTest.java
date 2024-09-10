package com.boardgo.integration.jwt.service;

import static org.assertj.core.api.Assertions.*;

import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.domain.user.entity.enums.RoleType;
import com.boardgo.domain.user.repository.UserRepository;
import com.boardgo.integration.data.UserInfoData;
import com.boardgo.integration.support.IntegrationTestSupport;
import com.boardgo.jwt.entity.AuthEntity;
import com.boardgo.jwt.repository.AuthRepository;
import com.boardgo.jwt.service.TokenService;
import com.boardgo.jwt.service.facade.AuthFacade;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

public class AuthFacadeTest extends IntegrationTestSupport {

    @Autowired private AuthFacade authFacade;
    @Autowired private TokenService tokenService;
    @Autowired private UserRepository userRepository;
    @Autowired private AuthRepository authRepository;
    @Autowired EntityManager entityManager;

    @Test
    @DisplayName("새로운 액세스 토큰과 리프레시 토큰을 발급 받을 수 있다")
    void 새로운_액세스_토큰과_리프레시_토큰을_발급_받을_수_있다() throws InterruptedException {
        // given
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

        UserInfoEntity user =
                userRepository.save(
                        UserInfoData.userInfoEntityData("aa@aa.com", "nickname").build());
        String refreshToken = tokenService.getRefreshToken(user.getId(), RoleType.USER);
        AuthEntity auth =
                authRepository.save(
                        AuthEntity.builder()
                                .userInfo(user)
                                .expirationDatetime(LocalDateTime.now().plusDays(1))
                                .refreshToken(refreshToken)
                                .build());
        // when
        Thread.sleep(1000);
        authFacade.reissue(refreshToken, response);
        // then
        entityManager.clear();
        AuthEntity authEntity = authRepository.findById(auth.getId()).get();
        assertThat(authEntity.getRefreshToken()).isNotEqualTo(refreshToken);
    }
}
