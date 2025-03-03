package com.pyeon.domain.member.facade;

import com.pyeon.domain.member.dao.MemberRepository;
import com.pyeon.domain.member.domain.Member;
import com.pyeon.global.exception.CustomException;
import com.pyeon.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Member 도메인에 대한 파사드 구현체
 */
@Component
@RequiredArgsConstructor
public class MemberFacadeImpl implements MemberFacade {
    private final MemberRepository memberRepository;
    
    @Override
    public Member getMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }
    
    @Override
    public boolean isMemberExists(Long id) {
        return memberRepository.existsById(id);
    }
} 