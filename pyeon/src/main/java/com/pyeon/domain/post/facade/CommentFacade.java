package com.pyeon.domain.post.facade;

import com.pyeon.domain.post.domain.Comment;

/**
 * Comment 도메인에 대한 파사드 인터페이스
 * 다른 도메인에서 Comment 도메인에 접근할 때 사용하는 인터페이스입니다.
 */
public interface CommentFacade {
    /**
     * ID로 댓글을 조회합니다.
     * 
     * @param id 댓글 ID
     * @return 조회된 댓글 엔티티
     * @throws com.pyeon.global.exception.CustomException 댓글이 존재하지 않을 경우
     */
    Comment getCommentById(Long id);
    
    /**
     * 댓글의 존재 여부를 확인합니다.
     * 
     * @param id 댓글 ID
     * @return 댓글 존재 여부
     */
    boolean isCommentExists(Long id);
} 