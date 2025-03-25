package com.pyeon.domain.auth.handler;

import com.pyeon.domain.auth.dto.response.TokenResponse;
import com.pyeon.domain.auth.service.AuthService;
import com.pyeon.global.exception.CustomException;
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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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
    ) {
        try {
            String targetUrl = handleSuccessfulAuthentication(authentication);
            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        } catch (Exception e) {
            handleAuthenticationError(request, response, e);
        }
    }

    private String handleSuccessfulAuthentication(Authentication authentication) {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        log.info("OAuth2User 정보: {}", oauth2User.getAttributes());
        
        TokenResponse tokenResponse = authService.createToken(oauth2User);
        return buildSuccessUrl(tokenResponse);
    }

    private String buildSuccessUrl(TokenResponse tokenResponse) {
        return UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("token", tokenResponse.getAccessToken())
                .build().toUriString();
    }

    private void handleAuthenticationError(
            HttpServletRequest request,
            HttpServletResponse response,
            Exception exception
    ) {
        String errorUrl = buildErrorUrl(exception);
        try {
            getRedirectStrategy().sendRedirect(request, response, errorUrl);
        } catch (Exception e) {
            log.error("리다이렉트 중 오류 발생", e);
        }
    }

    private String buildErrorUrl(Exception exception) {
        if (exception instanceof CustomException customException) {
            return buildCustomErrorUrl(customException);
        }
        return buildGenericErrorUrl();
    }

    private String buildCustomErrorUrl(CustomException exception) {
        String errorMessage = getErrorMessage(exception.getErrorCode().name());
        return UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("error", exception.getErrorCode().name())
                .queryParam("message", errorMessage)
                .build().toUriString();
    }

    private String buildGenericErrorUrl() {
        return UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("error", "AUTHENTICATION_FAILED")
                .queryParam("message", "Authentication failed")
                .build().toUriString();
    }

    private String getErrorMessage(String errorCode) {
        return switch (errorCode) {
            case "MEMBER_DEACTIVATED" -> "Account is deactivated";
            case "MEMBER_NOT_FOUND" -> "User not found";
            case "INVALID_TOKEN" -> "Invalid token";
            default -> "Authentication failed";
        };
    }
} 