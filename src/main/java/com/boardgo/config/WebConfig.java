package com.boardgo.config;

import com.boardgo.config.filter.LoggingFilter;
import com.boardgo.config.interceptor.LoggingInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// @Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final LoggingInterceptor interceptor;

    private static final String[] INTERCEPTOR_WHITE_LIST = {
        "/css/**", "/js/**", "/images/**", "/fonts/**", "/*.html", "/error", "/favicon.ico"
    };

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(INTERCEPTOR_WHITE_LIST);
    }

    @Bean
    public FilterRegistrationBean<LoggingFilter> addFilterSupporter() {
        FilterRegistrationBean<LoggingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new LoggingFilter());
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
}
