package com.pyeon.domain.auth.checker;

import com.pyeon.domain.auth.domain.UserPrincipal;
import com.pyeon.domain.member.dao.MemberRepository;
import com.pyeon.domain.member.domain.Member;
import com.pyeon.global.exception.CustomException;
import com.pyeon.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberAuthChecker {
    private final MemberRepository memberRepository;
    
    public boolean isOwner(Long memberId, UserPrincipal user) {
        if (user == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        
        if (!user.getId().equals(memberId)) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }
        
        return true;
    }

    public boolean canAccess(Long memberId, UserPrincipal user) {
        if (user == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        
        // ADMIN은 모든 회원 정보 접근 가능
        if (user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return true;
        }
        
        // 본인 정보만 접근 가능
        if (!isOwner(memberId, user)) {
            throw new CustomException(ErrorCode.ACCESS_DENIED);
        }
        
        return true;
    }

    public boolean isActive(Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
            
        if (!member.isActive()) {
            throw new CustomException(ErrorCode.MEMBER_DEACTIVATED);
        }
        
        return true;
    }
} 