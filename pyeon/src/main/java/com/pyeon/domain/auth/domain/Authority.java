package com.pyeon.domain.auth.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@Getter
@RequiredArgsConstructor
public enum Authority implements GrantedAuthority {
    
    ROLE_USER("ROLE_USER"),
    ROLE_ADMIN("ROLE_ADMIN");

    private final String authority;

    @Override
    public String getAuthority() {
        return authority;
    }
} 