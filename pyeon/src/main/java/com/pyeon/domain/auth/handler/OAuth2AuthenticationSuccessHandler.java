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
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import java.io.IOException;
import java.time.Duration;

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
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        TokenResponse tokenResponse = authService.createToken(oauth2User);

        // 쿠키 생성
        ResponseCookie cookie = ResponseCookie.from("accessToken", tokenResponse.getAccessToken())
                .httpOnly(true)
                .secure(true) // HTTPS에서만 전송
                .sameSite("Lax") // CSRF 보호
                .path("/") // 모든 경로에서 접근 가능
                .maxAge(Duration.ofHours(1)) // JWT 만료시간과 동일하게 설정
                .domain("localhost") // 개발 환경
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // 토큰 없이 프론트엔드로 리다이렉트
        getRedirectStrategy().sendRedirect(request, response, redirectUri);
    }
} 