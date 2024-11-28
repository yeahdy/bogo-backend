package com.boardgo.config;

import static com.boardgo.common.constant.HeaderConstant.AUTHORIZATION;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.POST;

import com.boardgo.domain.user.entity.enums.RoleType;
import com.boardgo.jwt.JWTFilter;
import com.boardgo.jwt.JWTUtil;
import com.boardgo.jwt.JwtExceptionHandlerFilter;
import com.boardgo.jwt.LoginFilter;
import com.boardgo.jwt.service.LoginService;
import com.boardgo.jwt.service.TokenService;
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
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final LoginService loginService;
    private final TokenService tokenService;
    private final JWTUtil jwtUtil;

    @Value("${spring.cors.origins}")
    private String corsOrigins;

    @Value("${spring.cors.methods}")
    private String corsMethods;

    @Value("${spring.cors.headers}")
    private String corsHeaders;

    final AntPathRequestMatcher[] permitAllUri = {
        AntPathRequestMatcher.antMatcher("/h2-console/**"),
        AntPathRequestMatcher.antMatcher("/resources/**"),
        AntPathRequestMatcher.antMatcher("/health"),
        AntPathRequestMatcher.antMatcher("/error"),
        AntPathRequestMatcher.antMatcher("/signup"),
        AntPathRequestMatcher.antMatcher("/login"),
        AntPathRequestMatcher.antMatcher("/docs/**"),
        AntPathRequestMatcher.antMatcher("/check-email"),
        AntPathRequestMatcher.antMatcher("/check-nickname"),
        AntPathRequestMatcher.antMatcher("/login/oauth2/**"),
        AntPathRequestMatcher.antMatcher("/token"),
        AntPathRequestMatcher.antMatcher("/actuator/**"),
        AntPathRequestMatcher.antMatcher(GET, "/meeting/**"),
        AntPathRequestMatcher.antMatcher(POST, "/reissue"),
        AntPathRequestMatcher.antMatcher(PATCH, "/meeting/share/{id}"),
        AntPathRequestMatcher.antMatcher(PATCH, "/meeting/complete/{id}"),
        AntPathRequestMatcher.antMatcher(POST, "/boardgame"),
        AntPathRequestMatcher.antMatcher(GET, "/personal-info/{userId}"),
        AntPathRequestMatcher.antMatcher(GET, "/home/**"),
        AntPathRequestMatcher.antMatcher(GET, "/terms-conditions/**")
    };

    final AntPathRequestMatcher[] permitUserUri = {
        AntPathRequestMatcher.antMatcher("/social/signup"),
        AntPathRequestMatcher.antMatcher("/personal-info/**"),
        AntPathRequestMatcher.antMatcher("/meeting-participant/**"),
        AntPathRequestMatcher.antMatcher("/evaluationTags"),
        AntPathRequestMatcher.antMatcher("/meeting/like"),
        AntPathRequestMatcher.antMatcher("/my/review/**"),
        AntPathRequestMatcher.antMatcher("/terms-conditions/user"),
        AntPathRequestMatcher.antMatcher("/notification/**"),
        AntPathRequestMatcher.antMatcher("/user-notification/**"),
        AntPathRequestMatcher.antMatcher("/push-token"),
        AntPathRequestMatcher.antMatcher("/user-terms-conditions/**")
    };

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
                                authenticationManager(authenticationConfiguration),
                                tokenService,
                                loginService),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class)
                .addFilterBefore(new JwtExceptionHandlerFilter(), JWTFilter.class)
                .authorizeHttpRequests(
                        authorize ->
                                authorize
                                        .requestMatchers(permitAllUri)
                                        .permitAll()
                                        .requestMatchers(permitUserUri)
                                        .hasRole(RoleType.USER.toString())
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
}
