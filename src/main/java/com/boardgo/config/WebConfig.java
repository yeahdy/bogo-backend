package com.boardgo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.boardgo.config.interceptor.LogInterceptor;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

	private final LogInterceptor interceptor;

	private static final String[] INTERCEPTOR_WHITE_LIST = {
		"/css/**", "/js/**", "/images/**", "/fonts/**", "/*.html", "/v3/api-docs"
	};

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(interceptor)
			.order(1)
			.excludePathPatterns(INTERCEPTOR_WHITE_LIST);
	}
}
