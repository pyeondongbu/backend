package com.pyeon.domain.auth.presentation;

import com.pyeon.domain.auth.domain.UserPrincipal;
import com.pyeon.domain.auth.dto.response.AuthUserResponse;
import com.pyeon.domain.auth.dto.response.TokenResponse;
import com.pyeon.domain.auth.service.AuthService;
import com.pyeon.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

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
} 