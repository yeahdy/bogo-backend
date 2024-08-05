package com.boardgo.config.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

public class LoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        CommonRequestWrapper httpServletRequestWrapper = new CommonRequestWrapper(request);
        ContentCachingResponseWrapper contentCachingResponseWrapper =
                new ContentCachingResponseWrapper(response);

        filterChain.doFilter(httpServletRequestWrapper, contentCachingResponseWrapper);
    }
}
