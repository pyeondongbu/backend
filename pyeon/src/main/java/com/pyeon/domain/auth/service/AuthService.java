package com.pyeon.domain.auth.service;

import com.pyeon.domain.auth.domain.UserPrincipal;
import com.pyeon.domain.auth.dto.response.TokenResponse;
import com.pyeon.domain.auth.util.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.oauth2.core.user.OAuth2User;
import com.pyeon.global.exception.CustomException;
import com.pyeon.global.exception.ErrorCode;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtProvider jwtProvider;

    @Transactional
    public TokenResponse createToken(OAuth2User oauth2User) {
        if (!(oauth2User instanceof UserPrincipal)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
        
        return new TokenResponse(
            jwtProvider.createAccessToken((UserPrincipal) oauth2User)
        );
    }
}