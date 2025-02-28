package com.pyeon.domain.admin.service;

import com.pyeon.domain.member.dao.MemberRepository;
import com.pyeon.domain.member.domain.Member;
import com.pyeon.domain.member.dto.response.MemberResponse;
import com.pyeon.global.exception.CustomException;
import com.pyeon.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminMemberServiceImpl implements AdminMemberService {
    
    private final MemberRepository memberRepository;

    @Override
    public Page<MemberResponse> getAllMembers(Pageable pageable) {
        return memberRepository.findAll(pageable)
                .map(MemberResponse::from);
    }

    @Override
    @Transactional
    public void suspendMember(Long memberId) {
        Member member = findMemberById(memberId);
        validateNotAdmin(member);
        member.suspend();
    }

    @Override
    @Transactional
    public void banMember(Long memberId) {
        Member member = findMemberById(memberId);
        validateNotAdmin(member);
        member.ban();
    }

    @Override
    @Transactional
    public void activateMember(Long memberId) {
        Member member = findMemberById(memberId);
        member.activate();
    }

    private Member findMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }

    private void validateNotAdmin(Member member) {
        if (member.isAdmin()) {
            throw new CustomException(ErrorCode.ACCESS_DENIED, "관리자는 제재할 수 없습니다.");
        }
    }
} 