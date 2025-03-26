package com.pyeon.domain.member.util;

import com.pyeon.domain.member.dao.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 닉네임 생성 및 중복 처리를 담당하는 유틸리티 클래스
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NicknameGenerator {
    
    private final MemberRepository memberRepository;
    
    /**
     * 중복되지 않는 고유한 닉네임을 생성합니다.
     * 기본 이름이 이미 존재할 경우 뒤에 숫자를 붙여서 고유성을 확보합니다.
     *
     * @param baseName 기본 닉네임
     * @return 고유한 닉네임
     */
    public String generateUniqueNickname(String baseName) {
        if (!memberRepository.existsByNickname(baseName)) {
            return baseName;
        }

        for (int i = 1; i <= 100; i++) {
            String candidateName = baseName + i;
            if (!memberRepository.existsByNickname(candidateName)) {
                return candidateName;
            }
        }

        return baseName + System.currentTimeMillis() % 10000;
    }
} 