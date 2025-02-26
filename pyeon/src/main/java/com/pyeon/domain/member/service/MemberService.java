package com.pyeon.domain.member.service;

import com.pyeon.domain.member.dto.request.MemberUpdateRequest;
import com.pyeon.domain.member.dto.response.MemberResponse;

public interface MemberService {
    MemberResponse getMember(Long id);
    void updateMember(Long id, MemberUpdateRequest request);
    void deleteMember(Long id);
} 