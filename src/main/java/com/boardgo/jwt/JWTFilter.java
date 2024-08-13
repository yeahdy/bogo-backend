package com.boardgo.jwt;

import static com.boardgo.common.constant.HeaderConstant.AUTHORIZATION;
import static com.boardgo.common.constant.HeaderConstant.BEARER;

import com.boardgo.domain.user.entity.UserInfoEntity;
import com.boardgo.domain.user.service.dto.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authorization = request.getHeader(AUTHORIZATION);

        if (isNull(authorization) || !authorization.startsWith(BEARER)) {
            log.info("Login Token is null");
            filterChain.doFilter(request, response);
            return;
        }

        String token = getToken(authorization);

        if (jwtUtil.isExpired(token)) {
            log.info("token is expired");
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authenticationToken = getAuthentication(token);

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
    }

    private Authentication getAuthentication(String token) {
        Long id = jwtUtil.getId(token);

        UserInfoEntity userInfoEntity = UserInfoEntity.builder().id(id).password(null).build();

        CustomUserDetails customUserDetails = new CustomUserDetails(userInfoEntity);

        return new UsernamePasswordAuthenticationToken(
                customUserDetails, null, customUserDetails.getAuthorities());
    }

    private static String getToken(String authorization) {
        return authorization.split(" ")[1];
    }

    private boolean isNull(String authorization) {
        return !Objects.nonNull(authorization);
    }
}
