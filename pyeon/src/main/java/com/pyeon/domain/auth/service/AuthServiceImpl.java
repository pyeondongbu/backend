package com.pyeon.domain.auth.service;

import com.pyeon.domain.auth.domain.UserPrincipal;
import com.pyeon.domain.auth.dto.response.TokenResponse;
import com.pyeon.domain.auth.util.JwtProvider;
import com.pyeon.domain.auth.util.UserPrincipalUtil;
import com.pyeon.domain.member.dao.MemberRepository;
import com.pyeon.domain.member.domain.Member;
import com.pyeon.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.oauth2.core.user.OAuth2User;
import com.pyeon.global.exception.CustomException;
import com.pyeon.global.exception.ErrorCode;

import java.util.Map;

/**
 * 인증 서비스 구현체
 * 사용자 인증 및 토큰 생성을 담당합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final JwtProvider jwtProvider;
    private final MemberService memberService;

    /**
     * OAuth2User 객체로부터 토큰을 생성합니다.
     */
    @Override
    @Transactional
    public TokenResponse createToken(OAuth2User oauth2User) {
        try {
            if (oauth2User instanceof UserPrincipal) {
                return createToken((UserPrincipal) oauth2User);
            } else {
                return createTokenFromOAuth2User(oauth2User);
            }
        } catch (CustomException e) {
            log.error("토큰 생성 중 오류 발생", e);
            throw e;
        } catch (Exception e) {
            log.error("토큰 생성 중 예상치 못한 오류 발생", e);
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }

    /**
     * UserPrincipal 객체로부터 토큰을 생성합니다.
     */
    @Override
    @Transactional
    public TokenResponse createToken(UserPrincipal userPrincipal) {
        return new TokenResponse(jwtProvider.createAccessToken(userPrincipal));
    }

    /**
     * OAuth2User 객체로부터 토큰을 생성합니다.
     */
    private TokenResponse createTokenFromOAuth2User(OAuth2User oauth2User) {
        Map<String, Object> attributes = oauth2User.getAttributes();
        String email = extractEmail(attributes);
        
        Member member = memberService.findOrCreateMemberByEmail(email, attributes);
        UserPrincipal userPrincipal = UserPrincipalUtil.createFromMember(member);
        
        return createToken(userPrincipal);
    }
    
    /**
     * OAuth2 속성에서 이메일을 추출합니다.
     */
    private String extractEmail(Map<String, Object> attributes) {
        String email = (String) attributes.get("email");
        if (email == null) {
            throw new CustomException(ErrorCode.OAUTH2_EMAIL_NOT_FOUND);
        }
        return email;
    }
} 