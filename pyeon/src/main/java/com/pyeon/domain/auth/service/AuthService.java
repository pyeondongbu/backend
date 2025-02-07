package com.pyeon.domain.auth.service;

import com.pyeon.domain.auth.domain.UserPrincipal;
import com.pyeon.domain.auth.dto.response.TokenResponse;
import com.pyeon.domain.auth.util.JwtProvider;
import com.pyeon.domain.member.dao.MemberRepository;
import com.pyeon.domain.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

    @Transactional
    public TokenResponse googleLogin(String code) {
        // OAuth2 인증은 SecurityConfig에서 처리되므로, 
        // 여기서는 토큰 발급만 처리합니다.
        throw new UnsupportedOperationException(
            "OAuth2 로그인은 /oauth2/authorization/google로 리다이렉트하여 처리해야 합니다."
        );
    }

    @Transactional
    public TokenResponse createToken(OAuth2User oauth2User) {
        UserPrincipal userPrincipal = (UserPrincipal) oauth2User;
        
        String accessToken = jwtProvider.createAccessToken(userPrincipal);
        
        return new TokenResponse(accessToken);
    }

    private Member createMember(Map<String, Object> attributes) {
        return memberRepository.save(Member.builder()
                .email((String) attributes.get("email"))
                .nickname((String) attributes.get("name"))
                .profileImageUrl((String) attributes.get("picture"))
                .build());
    }
} 