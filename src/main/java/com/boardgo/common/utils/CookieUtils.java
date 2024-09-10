package com.boardgo.common.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.Optional;
import org.springframework.http.ResponseCookie;

public abstract class CookieUtils {

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

    public static Cookie createCookies(String key, String value, Long duration) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(Math.toIntExact(duration * 60));
        cookie.setPath("/");
        cookie.setSecure(true);
        cookie.setHttpOnly(true);

        return cookie;
    }

    public static ResponseCookie createCookies(
            String name, String value, String domain, Long duration) {
        return ResponseCookie.from(name, value)
                .maxAge(Math.toIntExact(duration * 60))
                .path("/")
                .secure(true)
                .httpOnly(true)
                .domain(domain)
                .build();
    }
}
