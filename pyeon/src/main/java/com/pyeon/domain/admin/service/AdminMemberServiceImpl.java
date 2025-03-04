package com.pyeon.domain.admin.service;

import com.pyeon.domain.member.dao.MemberRepository;
import com.pyeon.domain.member.domain.Member;
import com.pyeon.domain.member.dto.response.MemberResponse;
import com.pyeon.global.exception.CustomException;
import com.pyeon.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminMemberServiceImpl implements AdminMemberService {
    
    private final MemberRepository memberRepository;
    
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<MemberResponse> getAllMembers(Pageable pageable) {
        return memberRepository.findAll(pageable)
                .map(MemberResponse::from);
    }

    @Override
    @Transactional
    public void deactivateMember(Long memberId) {
        Member member = findMemberById(memberId);
        member.delete();
    }

    @Override
    @Transactional
    public void activateMember(Long memberId) {
        // 네이티브 쿼리를 사용하여 직접 업데이트
        String updateQuery = "UPDATE member SET is_active = true WHERE id = :id";
        entityManager.createNativeQuery(updateQuery)
                .setParameter("id", memberId)
                .executeUpdate();
        
        // 캐시 초기화
        entityManager.clear();
    }
    
    @Override
    @Transactional
    public void promoteToAdmin(Long memberId) {
        // 네이티브 쿼리를 사용하여 직접 업데이트
        String updateQuery = "UPDATE member SET authority = 'ROLE_ADMIN' WHERE id = :id";
        entityManager.createNativeQuery(updateQuery)
                .setParameter("id", memberId)
                .executeUpdate();
        
        // 캐시 초기화
        entityManager.clear();
    }

    private Member findMemberById(Long id) {
        // 비활성화된 멤버도 포함하여 조회
        return memberRepository.findByIdIncludeInactive(id)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }
} 