package com.pyeon.domain.member.dao;


import com.pyeon.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT m FROM Member m WHERE m.refreshToken = :token")
    Optional<Member> findByRefreshToken(@Param("token") String token);

    @Query("SELECT m FROM Member m LEFT JOIN FETCH m.posts WHERE m.id = :id")
    Optional<Member> findByIdWithPosts(@Param("id") Long id);
}
