package com.pyeon.domain.auth.dto.response;

import com.pyeon.domain.auth.domain.UserPrincipal;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

@Getter
public class AuthUserResponse {
    private final Long id;
    private final String email;
    private final String nickname;
    private final String profileImageUrl;
    private final String authority;

    public AuthUserResponse(UserPrincipal userPrincipal) {
        this.id = userPrincipal.getId();
        this.email = userPrincipal.getEmail();
        this.nickname = userPrincipal.getNickname();
        this.profileImageUrl = userPrincipal.getProfileImageUrl();
        this.authority = userPrincipal.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse(null);
    }
} 