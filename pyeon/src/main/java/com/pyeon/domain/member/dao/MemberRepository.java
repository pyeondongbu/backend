package com.pyeon.domain.member.dao;


import com.pyeon.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * 활성화된 회원만 이메일로 찾습니다.
     */
    Optional<Member> findByEmail(String email);

    /**
     * 이메일 존재 여부를 확인합니다.
     */
    boolean existsByEmail(String email);
    
    /**
     * 닉네임 존재 여부를 확인합니다.
     */
    boolean existsByNickname(String nickname);
    
    /**
     * 모든 상태의 회원을 ID로 찾습니다.
     */
    @Query("SELECT m FROM Member m WHERE m.id = :id")
    Optional<Member> findByIdIncludeInactive(@Param("id") Long id);
    
    /**
     * 모든 상태의 회원을 이메일로 찾습니다.
     */
    @Query("SELECT m FROM Member m WHERE m.email = :email")
    Optional<Member> findByEmailIncludeInactive(@Param("email") String email);
}
