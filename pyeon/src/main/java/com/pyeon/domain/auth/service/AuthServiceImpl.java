package com.pyeon.domain.auth.service;

import com.pyeon.domain.auth.domain.UserPrincipal;
import com.pyeon.domain.auth.dto.response.TokenResponse;
import com.pyeon.domain.auth.util.JwtProvider;
import com.pyeon.domain.member.dao.MemberRepository;
import com.pyeon.domain.member.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.oauth2.core.user.OAuth2User;
import com.pyeon.global.exception.CustomException;
import com.pyeon.global.exception.ErrorCode;

import java.util.Collections;
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
    private final MemberRepository memberRepository;

    /**
     * OAuth2User 객체로부터 토큰을 생성합니다.
     *
     * @param oauth2User OAuth2 인증 정보
     * @return 생성된 토큰 정보
     */
    @Override
    @Transactional
    public TokenResponse createToken(OAuth2User oauth2User) {
        try {
            if (oauth2User instanceof UserPrincipal) {
                return createTokenFromUserPrincipal((UserPrincipal) oauth2User);
            } else {
                return createTokenFromOAuth2User(oauth2User);
            }
        } catch (Exception e) {
            log.error("토큰 생성 중 오류 발생", e);
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }

    /**
     * UserPrincipal 객체로부터 토큰을 생성합니다.
     *
     * @param userPrincipal 사용자 인증 주체 정보
     * @return 생성된 토큰 정보
     */
    @Override
    @Transactional
    public TokenResponse createToken(UserPrincipal userPrincipal) {
        return new TokenResponse(jwtProvider.createAccessToken(userPrincipal));
    }


    /**
     * Private 함수들
     */

    /**
     * UserPrincipal 객체로부터 토큰을 생성합니다.
     * 
     * @param userPrincipal 사용자 인증 주체 정보
     * @return 생성된 토큰 정보
     */
    private TokenResponse createTokenFromUserPrincipal(UserPrincipal userPrincipal) {
        return new TokenResponse(jwtProvider.createAccessToken(userPrincipal));
    }
    
    /**
     * OAuth2User 객체로부터 토큰을 생성합니다.
     * 사용자 정보를 추출하고 회원 정보를 조회한 후 토큰을 발급합니다.
     * 
     * @param oauth2User OAuth2 인증 정보
     * @return 생성된 토큰 정보
     */
    private TokenResponse createTokenFromOAuth2User(OAuth2User oauth2User) {
        Map<String, Object> attributes = oauth2User.getAttributes();
        String email = extractEmail(attributes);
        Member member = findOrCreateMember(email, attributes);
        UserPrincipal userPrincipal = createUserPrincipal(member);
        
        return new TokenResponse(jwtProvider.createAccessToken(userPrincipal));
    }
    
    /**
     * OAuth2 속성에서 이메일을 추출합니다.
     * 
     * @param attributes OAuth2 사용자 속성
     * @return 추출된 이메일
     * @throws CustomException 이메일이 없는 경우 발생
     */
    private String extractEmail(Map<String, Object> attributes) {
        String email = (String) attributes.get("email");
        if (email == null) {
            throw new CustomException(ErrorCode.OAUTH2_EMAIL_NOT_FOUND);
        }
        return email;
    }
    
    /**
     * 이메일로 회원 정보를 조회하고, 없으면 생성합니다.
     * 
     * @param email 이메일
     * @param attributes OAuth2 사용자 속성
     * @return 조회되거나 생성된 회원 정보
     */
    private Member findOrCreateMember(String email, Map<String, Object> attributes) {
        return memberRepository.findByEmail(email)
            .orElseGet(() -> createMember(email, attributes));
    }
    
    /**
     * 새 회원을 생성합니다.
     * 
     * @param email 이메일
     * @param attributes OAuth2 사용자 속성
     * @return 생성된 회원 정보
     */
    private Member createMember(String email, Map<String, Object> attributes) {
        String name = (String) attributes.get("name");
        String picture = (String) attributes.get("picture");
        String nickname = (name != null) ? name : email.split("@")[0];
        
        return memberRepository.save(
            Member.builder()
                .email(email)
                .nickname(nickname)
                .profileImageUrl(picture)
                .build()
        );
    }
    
    /**
     * 회원 정보로부터 UserPrincipal 객체를 생성합니다.
     * 
     * @param member 회원 정보
     * @return 생성된 UserPrincipal 객체
     */
    private UserPrincipal createUserPrincipal(Member member) {
        return UserPrincipal.builder()
            .id(member.getId())
            .email(member.getEmail())
            .nickname(member.getNickname())
            .profileImageUrl(member.getProfileImageUrl())
            .authorities(Collections.singleton(member.getAuthority()))
            .build();
    }
} 