package com.pyeon.domain.auth.presentation;

import com.pyeon.domain.auth.domain.UserPrincipal;
import com.pyeon.domain.auth.dto.response.AuthUserResponse;
import com.pyeon.domain.auth.dto.response.TokenResponse;
import com.pyeon.domain.auth.service.AuthService;
import com.pyeon.global.dto.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.beans.factory.annotation.Value;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Value("${app.cookie.secure}")
    private boolean secureCookie;
    
    @Value("${app.cookie.domain}")
    private String cookieDomain;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthUserResponse>> getCurrentUser(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        AuthUserResponse response = new AuthUserResponse(userPrincipal);
        return ResponseEntity.ok(ApiResponse.success(response, 200));
    }

    @GetMapping("/login/google/callback")
    public ResponseEntity<TokenResponse> googleCallback(
            @RequestParam String code,
            @RequestParam String state
    ) {
        TokenResponse tokenResponse = authService.googleLogin(code);
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(secureCookie)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .domain(cookieDomain)
                .build();
        
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        
        return ResponseEntity.ok(ApiResponse.success(null, 200, "로그아웃 되었습니다."));
    }
} 