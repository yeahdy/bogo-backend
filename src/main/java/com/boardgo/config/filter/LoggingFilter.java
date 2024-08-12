package com.boardgo.config.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

public class LoggingFilter extends OncePerRequestFilter {
    private final String MULTIPART_FORM = "multipart/form-data";
    private static final List<String> FILTER_WHITE_LIST = List.of("/h2-console");

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        CommonRequestWrapper httpServletRequestWrapper = new CommonRequestWrapper(request);
        ContentCachingResponseWrapper contentCachingResponseWrapper =
                new ContentCachingResponseWrapper(response);
        filterChain.doFilter(httpServletRequestWrapper, contentCachingResponseWrapper);

        contentCachingResponseWrapper.copyBodyToResponse();
    }

    private boolean existMultipartForm(HttpServletRequest request) {
        if (Objects.isNull(request.getContentType())) {
            return false;
        }
        return request.getContentType().toLowerCase().contains(MULTIPART_FORM);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        if (existMultipartForm(request)) {
            return true;
        }

        if (isWhiteList(request.getRequestURI())) {
            return true;
        }
        return false;
    }

    private boolean isWhiteList(String requestURI) {
        return FILTER_WHITE_LIST.stream()
                .anyMatch(whiteListedString -> requestURI.startsWith(whiteListedString));
    }
}
