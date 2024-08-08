package com.boardgo.config;

import static com.boardgo.common.constant.HeaderConstant.AUTHORIZATION;

import com.boardgo.jwt.JWTFilter;
import com.boardgo.jwt.JWTUtil;
import com.boardgo.jwt.LoginFilter;
import com.boardgo.oauth2.handler.OAuth2SuccessHandler;
import com.boardgo.oauth2.service.CustomOAuth2UserService;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;

    @Value("${spring.cors.origins}")
    private String corsOrigins;

    @Value("${spring.cors.methods}")
    private String corsMethods;

    @Value("${spring.cors.headers}")
    private String corsHeaders;

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
                .headers(
                        headersConfigurer -> {
                            headersConfigurer.frameOptions(frame -> frame.sameOrigin());
                        })
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
                                        .requestMatchers(
                                                "/h2-console/**",
                                                "/signup",
                                                "/login",
                                                "/docs/**",
                                                "/check-email",
                                                "/check-nickname",
                                                "/login/oauth2/**",
                                                "/token")
                                        .permitAll()
                                        .anyRequest()
                                        .authenticated())
                .oauth2Login(
                        (oauth2) -> {
                            oauth2.authorizationEndpoint(
                                            authorizationEndpointConfig -> {
                                                authorizationEndpointConfig.baseUri(
                                                        "/oauth2/authorization");
                                            })
                                    .redirectionEndpoint(
                                            redirectionEndpointConfig ->
                                                    redirectionEndpointConfig.baseUri(
                                                            "/login/oauth2/code/*"))
                                    .userInfoEndpoint(
                                            userInfoEndpointConfig -> {
                                                userInfoEndpointConfig.userService(
                                                        customOAuth2UserService);
                                            })
                                    .successHandler(oAuth2SuccessHandler);
                        })
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

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web ->
                web.ignoring()
                        .requestMatchers(
                                "/lib/**",
                                "/resources/**",
                                "/static/**",
                                "/css/**",
                                "/js/**",
                                "/img/**");
    }
}
