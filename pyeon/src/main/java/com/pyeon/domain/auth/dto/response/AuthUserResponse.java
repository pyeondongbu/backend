package com.pyeon.domain.auth.dto.response;

import com.pyeon.domain.auth.domain.UserPrincipal;
import com.pyeon.global.exception.CustomException;
import com.pyeon.global.exception.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
@Builder
public class AuthUserResponse {
    private final Long id;
    private final String email;
    private final String nickname;
    private final String profileImageUrl;
    private final String authority;

    public static AuthUserResponse from(UserPrincipal userPrincipal) {
        return AuthUserResponse.builder()
            .id(userPrincipal.getId())
            .email(userPrincipal.getEmail())
            .nickname(userPrincipal.getNickname())
            .profileImageUrl(userPrincipal.getProfileImageUrl())
            .authority(extractAuthority(userPrincipal.getAuthorities()))
            .build();
    }

    private static String extractAuthority(Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));
    }
} 