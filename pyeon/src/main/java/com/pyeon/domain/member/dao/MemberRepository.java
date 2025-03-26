package com.pyeon.domain.member.dao;


import com.pyeon.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);
    
    boolean existsByNickname(String nickname);
    
    // isActive 상태와 관계없이 ID로 멤버 찾기
    @Query("SELECT m FROM Member m WHERE m.id = :id")
    Optional<Member> findByIdIncludeInactive(@Param("id") Long id);
    
    // isActive 상태와 관계없이 이메일로 멤버 찾기
    @Query("SELECT m FROM Member m WHERE m.email = :email")
    Optional<Member> findByEmailIncludeInactive(@Param("email") String email);
    
    // 네이티브 SQL을 사용하여 모든 상태의 회원을 찾는 메서드
    @Query(value = "SELECT * FROM member WHERE email = :email", nativeQuery = true)
    Optional<Member> findByEmailWithNativeSql(@Param("email") String email);
}
