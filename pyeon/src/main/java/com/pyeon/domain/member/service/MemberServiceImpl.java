package com.pyeon.domain.member.service;

import com.pyeon.domain.member.dao.MemberRepository;
import com.pyeon.domain.member.domain.Member;
import com.pyeon.domain.member.dto.request.MemberUpdateRequest;
import com.pyeon.domain.member.dto.response.MemberResponse;
import com.pyeon.global.exception.CustomException;
import com.pyeon.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
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

    @Override
    @Transactional
    public Member findOrCreateMemberByEmail(String email, Map<String, Object> attributes) {
        Optional<Member> memberOpt = memberRepository.findByEmailWithNativeSql(email);
        
        if (memberOpt.isPresent()) {
            return validateAndGetMember(memberOpt.get(), email);
        } else {
            String nickname = extractNickname(attributes);
            String profileImageUrl = (String) attributes.get("picture");
            return createMember(email, nickname, profileImageUrl);
        }
    }
    
    @Override
    @Transactional
    public Member createMember(String email, String nickname, String profileImageUrl) {
        log.info("새로운 사용자 생성: {}", email);
        
        Member member = Member.builder()
                .email(email)
                .nickname(nickname)
                .profileImageUrl(profileImageUrl)
                .build();
                
        return memberRepository.save(member);
    }
    
    /**
     * 회원의 활성화 상태를 검증합니다.
     */
    private Member validateAndGetMember(Member member, String email) {
        if (!member.isActive()) {
            log.warn("비활성화된 계정으로 접근 시도: {}", email);
            throw new CustomException(ErrorCode.MEMBER_DEACTIVATED);
        }
        return member;
    }
    
    /**
     * OAuth2 속성에서 닉네임을 추출합니다.
     */
    private String extractNickname(Map<String, Object> attributes) {
        String name = (String) attributes.get("name");
        String email = (String) attributes.get("email");
        return (name != null) ? name : email.split("@")[0];
    }

    private Member findMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }
} 