package com.pyeon.domain.auth.service;

import com.pyeon.domain.auth.domain.UserPrincipal;
import com.pyeon.domain.auth.util.UserPrincipalUtil;
import com.pyeon.domain.auth.validator.OAuth2Validator;
import com.pyeon.domain.member.domain.Member;
import com.pyeon.domain.member.service.MemberService;
import com.pyeon.global.exception.CustomException;
import com.pyeon.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final MemberService memberService;
    private final OAuth2Validator validator;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        return processOAuth2User(userRequest, oauth2User);
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        Map<String, Object> attributes = oauth2User.getAttributes();
        
        validator.validateRegistrationId(userRequest);
        validator.validateAttributes(attributes);
        validator.validateEmail(attributes);

        String email = (String) attributes.get("email");
        Member member = memberService.findOrCreateMemberByEmail(email, attributes);
        
        return UserPrincipalUtil.createFromMember(member);
    }
} 