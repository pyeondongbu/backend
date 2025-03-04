package com.pyeon.domain.member.facade;

import com.pyeon.domain.member.domain.Member;

/**
 * Member 도메인에 대한 파사드 인터페이스
 * 다른 도메인에서 Member 도메인에 접근할 때 사용하는 인터페이스입니다.
 */
public interface MemberFacade {
    /**
     * ID로 회원을 조회합니다.
     * 
     * @param id 회원 ID
     * @return 조회된 회원 엔티티
     * @throws com.pyeon.global.exception.CustomException 회원이 존재하지 않을 경우
     */
    Member getMemberById(Long id);
    
    /**
     * ID로 회원을 조회합니다. (비활성화된 회원 포함)
     * 
     * @param id 회원 ID
     * @return 조회된 회원 엔티티
     * @throws com.pyeon.global.exception.CustomException 회원이 존재하지 않을 경우
     */
    Member getMemberByIdIncludeInactive(Long id);
    
    /**
     * 회원의 존재 여부를 확인합니다.
     * 
     * @param id 회원 ID
     * @return 회원 존재 여부
     */
    boolean isMemberExists(Long id);
} 