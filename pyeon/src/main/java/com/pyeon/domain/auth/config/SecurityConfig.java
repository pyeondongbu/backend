package com.pyeon.domain.auth.config;

import com.pyeon.domain.auth.filter.JwtAuthenticationFilter;
import com.pyeon.domain.auth.handler.JwtAccessDeniedHandler;
import com.pyeon.domain.auth.handler.JwtAuthenticationEntryPoint;
import com.pyeon.domain.auth.util.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

/**
 * Spring Security 설정 클래스
 * JWT 기반의 인증/인가 처리와 보안 설정을 담당
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final JwtAccessDeniedHandler accessDeniedHandler;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;

    /**
     * Spring Security FilterChain 설정
     * 인증/인가 처리, CORS, CSRF, 세션 관리 등 핵심 보안 설정 담당
     *
     * @param http HttpSecurity 객체
     * @return 구성된 SecurityFilterChain
     * @throws Exception 보안 설정 중 발생할 수 있는 예외
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 보호 비활성화 (REST API 서버이므로 불필요)
                .csrf(AbstractHttpConfigurer::disable)
                // 폼 로그인 비활성화 (JWT 사용)
                .formLogin(AbstractHttpConfigurer::disable)
                // HTTP Basic 인증 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)
                // 세션 설정: STATELESS로 설정하여 세션 사용하지 않음
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // URL별 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 인증 없이 접근 가능한 경로 설정
                        .requestMatchers("/api/auth/**", "/api/posts").permitAll()
                        // 관리자 권한이 필요한 경로 설정
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )
                // JWT 인증 필터 추가
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider),
                        UsernamePasswordAuthenticationFilter.class)
                // 예외 처리 설정
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(accessDeniedHandler)  // 권한 부족 시 처리
                        .authenticationEntryPoint(authenticationEntryPoint)  // 인증 실패 시 처리
                );

        return http.build();
    }

    /**
     * 역할 계층 구조 설정
     * ADMIN > USER 계층 구조 정의하여 ADMIN이 USER의 권한을 포함하도록 설정
     *
     * @deprecated RoleHierarchyImpl과 setHierarchy는 Deprecated 상태지만,
     *             현재 Spring Security에서 공식적인 대체 방안을 제공하지 않아 계속 사용
     */
    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");
        return hierarchy;
    }

    /**
     * Web Security 표현식 핸들러 설정
     * 역할 계층 구조를 반영한 권한 검사를 가능하게 함
     */
    @Bean
    public SecurityExpressionHandler<FilterInvocation> expressionHandler() {
        DefaultWebSecurityExpressionHandler handler = new DefaultWebSecurityExpressionHandler();
        handler.setRoleHierarchy(roleHierarchy());
        return handler;
    }

    /**
     * Method Security 표현식 핸들러 설정
     * @PreAuthorize 등의 애노테이션 기반 권한 검사에서 역할 계층 구조 적용
     */
    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
        handler.setRoleHierarchy(roleHierarchy());
        return handler;
    }

    /**
     * 패스워드 인코더 설정
     * BCrypt 해시 함수를 사용하여 패스워드를 안전하게 암호화
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}