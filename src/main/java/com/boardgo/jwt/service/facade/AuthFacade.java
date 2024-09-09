package com.boardgo.jwt.service.facade;

import static com.boardgo.common.constant.HeaderConstant.*;

import com.boardgo.common.exception.CustomUnAuthorizedException;
import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.jwt.entity.AuthEntity;
import com.boardgo.jwt.service.LoginService;
import com.boardgo.jwt.service.TokenService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthFacade implements AuthFacadeUseCase {
    private final TokenService tokenService;
    private final LoginService loginService;

    @Value("${oauth-login.redirect.domain}")
    private String domain;

    @Override
    @Transactional
    public void reissue(String refreshToken, HttpServletResponse response) {
        if (tokenService.isInvalidRefreshToken(refreshToken)) {
            throw new CustomUnAuthorizedException("토큰에 문제가 있습니다.");
        }

        AuthEntity authEntity = loginService.getByRefreshToken(refreshToken);
        UserInfoEntity user = authEntity.getUserInfo();
        String accessToken = tokenService.getAccessToken(user.getId(), refreshToken);
        String newRefreshToken = tokenService.getRefreshToken(user.getId(), refreshToken, domain);

        loginService.updateTokenWithOutValidation(authEntity, refreshToken, user.getId());
        response.addHeader(AUTHORIZATION, BEARER + accessToken);
        response.setHeader("Set-cookie", newRefreshToken);
    }
}
