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

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;

    @Transactional
    public TokenResponse createToken(OAuth2User oauth2User) {
        try {
            log.info("OAuth2User 타입: {}", oauth2User.getClass().getName());
            
            if (oauth2User instanceof UserPrincipal) {
                log.info("OAuth2User는 UserPrincipal 타입입니다.");
                return new TokenResponse(
                    jwtProvider.createAccessToken((UserPrincipal) oauth2User)
                );
            } else {
                log.info("OAuth2User는 UserPrincipal 타입이 아닙니다. 사용자 정보를 추출합니다.");
                // OAuth2User에서 필요한 정보 추출
                Map<String, Object> attributes = oauth2User.getAttributes();
                log.info("OAuth2User 속성: {}", attributes);
                
                String email = (String) attributes.get("email");
                if (email == null) {
                    log.error("이메일 정보를 찾을 수 없습니다.");
                    throw new CustomException(ErrorCode.OAUTH2_EMAIL_NOT_FOUND);
                }
                
                // 회원 정보 조회
                Member member = memberRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        log.error("회원 정보를 찾을 수 없습니다: {}", email);
                        return new CustomException(ErrorCode.MEMBER_NOT_FOUND);
                    });
                
                log.info("회원 정보를 찾았습니다: {}", member.getEmail());
                
                // UserPrincipal 생성
                UserPrincipal userPrincipal = UserPrincipal.builder()
                    .id(member.getId())
                    .email(member.getEmail())
                    .nickname(member.getNickname())
                    .profileImageUrl(member.getProfileImageUrl())
                    .authorities(Collections.singleton(member.getAuthority()))
                    .build();
                
                log.info("UserPrincipal 생성 완료: {}", userPrincipal.getEmail());
                
                return new TokenResponse(
                    jwtProvider.createAccessToken(userPrincipal)
                );
            }
        } catch (Exception e) {
            log.error("토큰 생성 중 오류 발생", e);
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }
    
    @Transactional
    public TokenResponse createToken(UserPrincipal userPrincipal) {
        return new TokenResponse(
            jwtProvider.createAccessToken(userPrincipal)
        );
    }
}