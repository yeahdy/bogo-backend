package com.boardgo.config.filter;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FilterSupporter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		CommonRequestWrapper httpServletRequestWrapper = new CommonRequestWrapper(request);
		ContentCachingResponseWrapper contentCachingResponseWrapper = new ContentCachingResponseWrapper(response);

		filterChain.doFilter(httpServletRequestWrapper, contentCachingResponseWrapper);
	}
}
