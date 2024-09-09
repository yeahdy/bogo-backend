package com.boardgo.jwt.service;

import static com.boardgo.common.constant.HeaderConstant.*;
import static com.boardgo.common.constant.NameConstant.*;
import static com.boardgo.common.constant.TimeConstant.*;
import static com.boardgo.common.utils.CookieUtils.*;

import com.boardgo.common.exception.CustomUnAuthorizedException;
import com.boardgo.domain.user.entity.enums.RoleType;
import com.boardgo.jwt.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final JWTUtil jwtUtil;

    public String getAccessToken(Long id, String token) {
        validateEqualId(id, token);

        RoleType role = RoleType.valueOf(jwtUtil.getRole(token));
        return jwtUtil.createJwt(id, ACCESS_TOKEN, role, ACCESS_TOKEN_DURATION);
    }

    public String getAccessToken(Long id, RoleType role) {
        return jwtUtil.createJwt(id, ACCESS_TOKEN, role, ACCESS_TOKEN_DURATION);
    }

    public String getRefreshToken(Long id, String token, String domain) {
        validateEqualId(id, token);

        RoleType role = RoleType.valueOf(jwtUtil.getRole(token));
        String refreshToken = jwtUtil.createJwt(id, REFRESH_TOKEN, role, REFRESH_TOKEN_DURATION);
        return createCookies(AUTHORIZATION, refreshToken, domain, REFRESH_TOKEN_DURATION)
                .toString();
    }

    public String getRefreshToken(Long id, RoleType roleType, String domain) {
        String refreshToken =
                jwtUtil.createJwt(id, REFRESH_TOKEN, roleType, REFRESH_TOKEN_DURATION);
        return createCookies(AUTHORIZATION, refreshToken, domain, REFRESH_TOKEN_DURATION)
                .toString();
    }

    public boolean isInvalidRefreshToken(String refreshToken) {
        return Optional.ofNullable(refreshToken)
                .map(token -> isExpired(token) || isNotRefreshToken(token))
                .orElse(true);
    }

    private void validateEqualId(Long id, String token) {
        if (!jwtUtil.getId(token).equals(id)) {
            throw new CustomUnAuthorizedException("토큰에 문제가 있습니다.");
        }
    }

    private boolean isNotRefreshToken(String refreshToken) {
        return !jwtUtil.getCategory(refreshToken).equals(REFRESH_TOKEN);
    }

    private boolean isExpired(String refreshToken) {
        try {
            jwtUtil.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            return true;
        }
        return false;
    }
}
