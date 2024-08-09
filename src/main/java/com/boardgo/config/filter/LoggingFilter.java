package com.boardgo.config.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

public class LoggingFilter extends OncePerRequestFilter {
    private final String MULTIPART_FORM = "multipart/form-data";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (existMultipartForm(request)) {
            filterChain.doFilter(request, response);
        }

        CommonRequestWrapper httpServletRequestWrapper = new CommonRequestWrapper(request);
        ContentCachingResponseWrapper contentCachingResponseWrapper =
                new ContentCachingResponseWrapper(response);
        filterChain.doFilter(httpServletRequestWrapper, contentCachingResponseWrapper);
    }

    private boolean existMultipartForm(HttpServletRequest request) {
        if (Objects.isNull(request.getContentType())) {
            return false;
        }
        return request.getContentType().toLowerCase().contains(MULTIPART_FORM);
    }
}
