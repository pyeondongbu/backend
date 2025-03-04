package com.pyeon.domain.admin.service;

import com.pyeon.domain.member.dto.response.MemberResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminMemberService {
    Page<MemberResponse> getAllMembers(Pageable pageable);
    void deactivateMember(Long memberId);  // 계정 정지
    void activateMember(Long memberId);    // 정지 해제
    void promoteToAdmin(Long memberId);    // 관리자 권한 부여
}