package com.pyeon.domain.auth.handler;

import com.pyeon.domain.auth.dto.response.TokenResponse;
import com.pyeon.domain.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.util.UriComponentsBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final AuthService authService;
    
    @Value("${app.oauth2.authorized-redirect-uri}")
    private String redirectUri;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        log.info("OAuth2 인증 성공: {}", authentication.getName());
        log.info("인증 객체 클래스: {}", authentication.getClass().getName());
        log.info("인증 객체 세부 정보: {}", authentication.getDetails());
        log.info("인증 객체 권한: {}", authentication.getAuthorities());
        
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        log.info("OAuth2User 클래스: {}", oauth2User.getClass().getName());
        log.info("OAuth2User 정보: {}", oauth2User.getAttributes());
        
        log.info("리다이렉트 URI 설정값: {}", redirectUri);
        
        try {
            TokenResponse tokenResponse = authService.createToken(oauth2User);
            log.info("토큰 생성 완료: {}", tokenResponse.getAccessToken().substring(0, 10) + "...");

            // 토큰을 URL 파라미터로 추가하여 리다이렉트
            String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                    .queryParam("token", tokenResponse.getAccessToken())
                    .build().toUriString();
            
            log.info("최종 리다이렉트 URL: {}", targetUrl);

            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        } catch (Exception e) {
            log.error("인증 성공 처리 중 오류 발생", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "인증 처리 중 오류가 발생했습니다.");
        }
    }
} 