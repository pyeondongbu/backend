package com.pyeon.domain.auth.util;

import com.pyeon.domain.auth.domain.UserPrincipal;
import com.pyeon.domain.member.domain.Member;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * UserPrincipal 생성 유틸리티 클래스
 */
@Component
public class UserPrincipalUtil {
    
    /**
     * Member 엔티티로부터 UserPrincipal 생성
     */
    public static UserPrincipal createFromMember(Member member) {
        return UserPrincipal.builder()
                .id(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .profileImageUrl(member.getProfileImageUrl())
                .authorities(Collections.singleton(member.getAuthority()))
                .build();
    }
} 