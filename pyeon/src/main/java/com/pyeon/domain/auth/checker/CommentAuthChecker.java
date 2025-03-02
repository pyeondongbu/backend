package com.pyeon.domain.auth.checker;

import com.pyeon.domain.auth.domain.UserPrincipal;
import com.pyeon.domain.post.dao.CommentRepository;
import com.pyeon.domain.post.domain.Comment;
import com.pyeon.global.exception.CustomException;
import com.pyeon.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CommentAuthChecker {
    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    public boolean canModify(Long commentId, UserPrincipal principal) {
        if (principal == null) {
            return false;
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        return comment.getMember().getId().equals(principal.getId());
    }
} 