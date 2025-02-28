package com.pyeon.domain.auth.checker;

import com.pyeon.domain.auth.domain.UserPrincipal;
import com.pyeon.domain.post.dao.PostRepository;
import com.pyeon.domain.member.dao.MemberRepository;
import com.pyeon.domain.member.domain.Member;
import com.pyeon.global.exception.CustomException;
import com.pyeon.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostAuthChecker {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    
    public boolean isOwner(Long postId, UserPrincipal user) {
        if (user == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        
        return postRepository.findById(postId)
            .map(post -> post.getMember().getId().equals(user.getId()))
            .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
    }

    public boolean canModify(Long postId, UserPrincipal user) {
        if (user == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }
        
        // ADMIN은 모든 게시글 수정/삭제 가능
        if (user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return true;
        }
        
        if (!isOwner(postId, user)) {
            throw new CustomException(ErrorCode.NOT_POST_AUTHOR);
        }
        
        return true;
    }

    public boolean canWrite(UserPrincipal user) {
        if (user == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        Member member = memberRepository.findById(user.getId())
            .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        if (!member.isActive()) {
            throw new CustomException(ErrorCode.MEMBER_DEACTIVATED);
        }

        return true;
    }
} 