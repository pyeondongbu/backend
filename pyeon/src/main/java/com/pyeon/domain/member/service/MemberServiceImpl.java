package com.pyeon.domain.member.service;

import com.pyeon.domain.member.dao.MemberRepository;
import com.pyeon.domain.member.domain.Member;
import com.pyeon.domain.member.dto.request.MemberUpdateRequest;
import com.pyeon.domain.member.dto.response.MemberResponse;
import com.pyeon.global.exception.CustomException;
import com.pyeon.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;

    @Override
    public MemberResponse getMember(Long id) {
        Member member = findMemberById(id);
        return MemberResponse.from(member);
    }

    @Override
    @Transactional
    public void updateMember(Long id, MemberUpdateRequest request) {
        Member member = findMemberById(id);
        
        // 닉네임 중복 검사
        String nickname = request.getNickname();
        if (!member.getNickname().equals(nickname) && memberRepository.existsByNickname(nickname)) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }
        
        member.updateProfile(
                nickname,
                request.getProfileImageUrl()
        );
    }

    @Override
    @Transactional
    public void deleteMember(Long id) {
        Member member = findMemberById(id);
        member.delete();
    }

    private Member findMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }
} 