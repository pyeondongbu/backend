package com.pyeon.domain.auth.service;

import com.pyeon.domain.auth.domain.UserPrincipal;
import com.pyeon.domain.member.dao.MemberRepository;
import com.pyeon.domain.member.domain.Member;
import com.pyeon.domain.auth.validator.OAuth2Validator;
import com.pyeon.global.exception.CustomException;
import com.pyeon.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;
    private final OAuth2Validator validator;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        return processOAuth2User(userRequest, oauth2User);
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        Map<String, Object> attributes = oauth2User.getAttributes();
        
        validator.validateRegistrationId(userRequest);
        validator.validateAttributes(attributes);
        validator.validateEmail(attributes);

        Member member = findOrCreateMember(attributes);
        return createUserPrincipal(member);
    }

    private Member findOrCreateMember(Map<String, Object> attributes) {
        String email = (String) attributes.get("email");
        
        Optional<Member> memberOpt = memberRepository.findByEmailIncludeInactive(email);
        
        if (memberOpt.isPresent()) {
            Member member = memberOpt.get();
            if (!member.isActive()) {
                throw new CustomException(ErrorCode.MEMBER_DEACTIVATED);
            }
            return member;
        }
        
        // 계정이 없으면 새로 생성
        return createMember(attributes);
    }

    private Member createMember(Map<String, Object> attributes) {
        return memberRepository.save(
            Member.builder()
                .email((String) attributes.get("email"))
                .nickname(extractNickname(attributes))
                .profileImageUrl((String) attributes.get("picture"))
                .build()
        );
    }

    private String extractNickname(Map<String, Object> attributes) {
        String name = (String) attributes.get("name");
        String email = (String) attributes.get("email");
        return (name != null) ? name : email.split("@")[0];
    }

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