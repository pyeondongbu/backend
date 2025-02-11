package com.pyeon.domain.auth.service;

import com.pyeon.domain.auth.domain.Authority;
import com.pyeon.domain.auth.domain.UserPrincipal;
import com.pyeon.domain.member.dao.MemberRepository;
import com.pyeon.domain.member.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        
        try {
            return this.process(userRequest, oauth2User);
        } catch (Exception ex) {
            log.error("OAuth2 로그인 처리 중 에러 발생: ", ex);
            throw new OAuth2AuthenticationException(ex.getMessage());
        }
    }

    private OAuth2User process(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        
        if (!"google".equals(registrationId)) {
            throw new OAuth2AuthenticationException("Google 로그인만 지원합니다");
        }

        Map<String, Object> attributes = oauth2User.getAttributes();
        
        String email = (String) attributes.get("email");
        if (!Boolean.TRUE.equals(attributes.get("email_verified"))) {
            throw new OAuth2AuthenticationException("이메일 인증이 필요합니다");
        }

        Member member = memberRepository.findByEmail(email)
                .orElseGet(() -> createMember(attributes));

        return new UserPrincipal(
                member.getId(),
                member.getEmail(),
                member.getNickname(),
                member.getProfileImageUrl(),
                Collections.singleton(member.getAuthority())
        );
    }

    private Member createMember(Map<String, Object> attributes) {

        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String picture = (String) attributes.get("picture");

        String nickname = (name != null) ? name : email.split("@")[0];

        return memberRepository.save(Member.builder()
                .email(email)
                .nickname(nickname)
                .profileImageUrl(picture)
                .build());
    }
} 