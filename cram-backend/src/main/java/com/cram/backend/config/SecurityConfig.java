package com.cram.backend.config;

import com.cram.backend.jwt.CustomLogoutFilter;
import com.cram.backend.jwt.JWTFilter;
import com.cram.backend.jwt.JWTUtil;
import com.cram.backend.oauth2.CustomSuccessHandler;
import com.cram.backend.user.repository.RefreshRepository;
import com.cram.backend.user.service.CustomOAuth2UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final String[] ALLOW_URL = {
            "/h2-console/**",
            "/chat/**",                // 정적 HTML: /chat/GroupChat.html 추후 삭제
            "/api/token",              // JWT 토큰 발급 API 추후 삭제
            "/v3/api-docs/**",// swagger 경로
            "/swagger-ui/**",
            "/swagger-ui.html",
    };

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService, CustomSuccessHandler customSuccessHandler, JWTUtil jwtUtil, RefreshRepository refreshRepository) {

        this.customOAuth2UserService = customOAuth2UserService;
        this.customSuccessHandler = customSuccessHandler;
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }

    @Bean
    public JWTFilter jwtFilter() {
        return new JWTFilter(jwtUtil);
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.cors(corsCustomizer -> corsCustomizer.configurationSource(request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedOriginPatterns(List.of("*")); // ✅ 이건 와일드카드로 허용 가능
            config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            config.setAllowedHeaders(List.of("*"));
            config.setExposedHeaders(List.of("Authorization", "Set-Cookie"));
            config.setAllowCredentials(true); // ✅ 인증 포함 허용
            config.setMaxAge(3600L);
            return config;
        }));

        //csrf disable
        http
                .csrf((auth) -> auth.disable());

        http.headers(headers -> headers.defaultsDisabled().disable()); // 혹은 frameOptions().disable()이 포함


        //From 로그인 방식 disable
        http
                .formLogin((auth) -> auth.disable());

        //http basic 인증 방식 disable
        http
                .httpBasic((auth) -> auth.disable());

        http.exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Unauthorized or invalid token.\"}");
                })
        );

        //JWTFilter 추가
        http
                .addFilterAfter(jwtFilter(), OAuth2LoginAuthenticationFilter.class);

        // LogoutFilter 추가
        http
                .addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshRepository), LogoutFilter.class);

        //oauth2
        http
                .oauth2Login((oauth2) -> oauth2
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                                .userService(customOAuth2UserService))
                        .successHandler(customSuccessHandler)
                );

        //경로별 인가 작업
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(ALLOW_URL).permitAll()
                        .anyRequest().authenticated());

        //세션 설정
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
