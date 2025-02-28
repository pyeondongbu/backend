package com.pyeon.domain.admin.service;

import com.pyeon.domain.member.dto.response.MemberResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminMemberService {
    Page<MemberResponse> getAllMembers(Pageable pageable);
    void suspendMember(Long memberId);
    void banMember(Long memberId);
    void activateMember(Long memberId);
}