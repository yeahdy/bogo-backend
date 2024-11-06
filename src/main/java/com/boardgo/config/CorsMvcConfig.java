package com.boardgo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsMvcConfig implements WebMvcConfigurer {

    @Value("${spring.cors.origins}")
    private String corsOrigins;

    @Value("${spring.cors.methods}")
    private String corsMethods;

    @Value("${spring.cors.headers}")
    private String corsHeaders;

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/**")
                .allowedOriginPatterns(corsOrigins)
                .allowedHeaders(corsHeaders)
                .allowedMethods(corsMethods)
                .exposedHeaders("Set-Cookie", "JSESSIONID")
                .allowCredentials(true);
    }
}
