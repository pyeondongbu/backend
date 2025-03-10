package com.pyeon.domain.auth.presentation;

import com.pyeon.domain.auth.domain.UserPrincipal;
import com.pyeon.domain.auth.dto.response.AuthUserResponse;
import com.pyeon.domain.auth.dto.response.TokenResponse;
import com.pyeon.domain.auth.service.AuthService;
import com.pyeon.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthUserResponse>> getCurrentUser(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(AuthUserResponse.from(userPrincipal), 200)
        );
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/token")
    public ResponseEntity<ApiResponse<TokenResponse>> refreshToken(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        TokenResponse tokenResponse = authService.createToken(userPrincipal);
        return ResponseEntity.ok(
                ApiResponse.success(tokenResponse, 200, "토큰이 성공적으로 발급되었습니다.")
        );
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        // 클라이언트에서 토큰을 삭제하도록 안내하는 메시지만 반환
        return ResponseEntity.ok(
                ApiResponse.success(null, 200, "로그아웃 되었습니다. 클라이언트에서 토큰을 삭제해주세요.")
        );
    }
}