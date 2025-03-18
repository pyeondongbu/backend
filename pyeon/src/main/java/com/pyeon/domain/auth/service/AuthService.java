package com.pyeon.domain.auth.service;

import com.pyeon.domain.auth.domain.UserPrincipal;
import com.pyeon.domain.auth.dto.response.TokenResponse;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * 인증 관련 서비스 인터페이스
 * 사용자 인증 정보를 기반으로 토큰을 생성하는 기능을 제공합니다.
 */
public interface AuthService {
    
    /**
     * OAuth2User 객체로부터 토큰을 생성합니다.
     *
     * @param oauth2User OAuth2 인증 정보
     * @return 생성된 토큰 정보
     */
    TokenResponse createToken(OAuth2User oauth2User);
    
    /**
     * UserPrincipal 객체로부터 토큰을 생성합니다.
     *
     * @param userPrincipal 사용자 인증 주체 정보
     * @return 생성된 토큰 정보
     */
    TokenResponse createToken(UserPrincipal userPrincipal);
}