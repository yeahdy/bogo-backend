package com.boardgo.integration.jwt.service;

import com.boardgo.domain.user.entity.enums.RoleType;
import com.boardgo.jwt.JWTUtil;
import com.boardgo.jwt.service.TokenService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TokenServiceTest {
    @Test
    @DisplayName("새로 발급한 리프레시 토큰은 기존 리프레시 토큰과 다르다")
    void 새로_발급한_리프레시_토큰은_기존_리프레시_토큰과_다르다() throws InterruptedException {
        // given
        TokenService tokenService =
                new TokenService(new JWTUtil("abcasfdsfasdfasfasdasdfasdfsadfsadfasdfsadfsdaff"));
        String refreshToken = tokenService.getRefreshToken(1L, RoleType.USER);
        // when
        Thread.sleep(1000);
        String result = tokenService.getRefreshToken(1L, RoleType.USER);
        // then
        Assertions.assertThat(refreshToken).isNotEqualTo(result);
    }
}
