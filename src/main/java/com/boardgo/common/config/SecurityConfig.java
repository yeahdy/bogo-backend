package com.boardgo.common.config;

import static com.boardgo.common.constant.HeaderConstant.*;

import com.boardgo.jwt.JWTFilter;
import com.boardgo.jwt.JWTUtil;
import com.boardgo.jwt.LoginFilter;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;

    @Value("${spring.cors.origins}")
    private String corsOrigins;

    @Value("${spring.cors.methods}")
    private String corsMethods;

    @Value("${spring.cors.headers}")
    private String corsHeaders;

    public SecurityConfig(
            AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtUtil) {
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .cors(
                        cors ->
                                cors.configurationSource(
                                        request -> {
                                            CorsConfiguration configuration =
                                                    new CorsConfiguration();

                                            configuration.setAllowedOriginPatterns(
                                                    Collections.singletonList(corsOrigins));
                                            configuration.setExposedHeaders(
                                                    Collections.singletonList(corsHeaders));
                                            configuration.setAllowedMethods(
                                                    Collections.singletonList(corsMethods));
                                            configuration.setAllowCredentials(true);

                                            configuration.setExposedHeaders(
                                                    Collections.singletonList(AUTHORIZATION));

                                            return configuration;
                                        }))
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(
                        sessionManagerConfigurer ->
                                sessionManagerConfigurer.sessionCreationPolicy(
                                        SessionCreationPolicy.STATELESS))
                .addFilterAt(
                        new LoginFilter(
                                authenticationManager(authenticationConfiguration), jwtUtil),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class)
                .authorizeHttpRequests(
                        authorize ->
                                authorize
                                        .requestMatchers("/signup", "/login")
                                        .permitAll()
                                        // TODO: 나중에 permitAll 없애기
                                        .anyRequest()
                                        .authenticated())
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
