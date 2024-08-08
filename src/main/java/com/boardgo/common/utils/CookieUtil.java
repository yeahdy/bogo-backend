package com.boardgo.common.utils;

import static com.boardgo.common.constant.TimeConstant.ACCESS_TOKEN_DURATION;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.Optional;

public abstract class CookieUtil {

    public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();

        if (Objects.isNull(cookies)) {
            return Optional.empty();
        }
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                return Optional.of(cookie);
            }
        }
        return Optional.empty();
    }

    public static Cookie createCookies(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(Math.toIntExact(ACCESS_TOKEN_DURATION));
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}
