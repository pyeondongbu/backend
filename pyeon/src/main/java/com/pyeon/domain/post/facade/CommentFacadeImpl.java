package com.pyeon.domain.post.facade;

import com.pyeon.domain.post.dao.CommentRepository;
import com.pyeon.domain.post.domain.Comment;
import com.pyeon.global.exception.CustomException;
import com.pyeon.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Comment 도메인에 대한 파사드 구현체
 */
@Component
@RequiredArgsConstructor
public class CommentFacadeImpl implements CommentFacade {
    private final CommentRepository commentRepository;
    
    @Override
    public Comment getCommentById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
    }
    
    @Override
    public boolean isCommentExists(Long id) {
        return commentRepository.existsById(id);
    }
} 