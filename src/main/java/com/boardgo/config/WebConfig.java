package com.boardgo.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.boardgo.config.filter.LoggingFilter;
import com.boardgo.config.interceptor.LoggingInterceptor;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

	private final LoggingInterceptor interceptor;

	private static final String[] INTERCEPTOR_WHITE_LIST = {
		"/css/**", "/js/**", "/images/**", "/fonts/**", "/*.html"
	};

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(interceptor)
			.order(1)
			.excludePathPatterns(INTERCEPTOR_WHITE_LIST);
	}

	@Bean
	public FilterRegistrationBean<LoggingFilter> addFilterSupporter() {
		FilterRegistrationBean<LoggingFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new LoggingFilter());
		return registrationBean;
	}
}
