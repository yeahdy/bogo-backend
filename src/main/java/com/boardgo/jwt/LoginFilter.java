package com.boardgo.jwt;

import static com.boardgo.common.constant.HeaderConstant.*;
import static com.boardgo.common.constant.TimeConstant.*;
import static com.boardgo.common.utils.CookieUtils.*;

import com.boardgo.domain.user.entity.enums.RoleType;
import com.boardgo.domain.user.service.response.CustomUserDetails;
import com.boardgo.jwt.service.LoginService;
import com.boardgo.jwt.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final LoginService loginService;

    @Value("${oauth-login.redirect.domain}")
    private String domain;

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        String username = obtainUsername(request);
        String password = obtainPassword(request);

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(username, password, null);

        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult)
            throws IOException, ServletException {
        CustomUserDetails customUserDetails = (CustomUserDetails) authResult.getPrincipal();

        Long id = customUserDetails.getId();
        Collection<? extends GrantedAuthority> authorities = authResult.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        RoleType role = RoleType.toRoleType(auth.getAuthority());

        String accessToken = tokenService.getAccessToken(id, role);
        String refreshToken = tokenService.getRefreshToken(id, role);
        loginService.create(refreshToken, id);
        response.addHeader(AUTHORIZATION, BEARER + accessToken);
        response.setHeader(
                "Set-cookie",
                createCookies(AUTHORIZATION, refreshToken, domain, REFRESH_TOKEN_DURATION)
                        .toString());
    }

    @Override
    protected void unsuccessfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException failed)
            throws IOException, ServletException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
    }
}
