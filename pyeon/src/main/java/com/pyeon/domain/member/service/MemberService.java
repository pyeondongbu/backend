package com.pyeon.domain.member.service;

import com.pyeon.domain.member.domain.Member;
import com.pyeon.domain.member.dto.request.MemberUpdateRequest;
import com.pyeon.domain.member.dto.response.MemberResponse;

import java.util.Map;

public interface MemberService {
    MemberResponse getMember(Long id);
    void updateMember(Long id, MemberUpdateRequest request);
    void deleteMember(Long id);
    
    /**
     * OAuth2 인증 정보로 회원을 생성하거나 찾습니다.
     */
    Member findOrCreateMemberByEmail(String email, Map<String, Object> attributes);
    
    /**
     * 새로운 회원을 생성합니다.
     */
    Member createMember(String email, String nickname, String profileImageUrl);
} 