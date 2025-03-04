package com.pyeon.domain.member.dao;


import com.pyeon.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);
    
    // isActive 상태와 관계없이 ID로 멤버 찾기
    @Query("SELECT m FROM Member m WHERE m.id = :id")
    Optional<Member> findByIdIncludeInactive(@Param("id") Long id);
    
    // isActive 상태와 관계없이 이메일로 멤버 찾기
    @Query("SELECT m FROM Member m WHERE m.email = :email")
    Optional<Member> findByEmailIncludeInactive(@Param("email") String email);
}
