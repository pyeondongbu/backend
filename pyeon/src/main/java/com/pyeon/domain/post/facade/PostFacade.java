package com.pyeon.domain.post.facade;

import com.pyeon.domain.post.domain.Post;

/**
 * Post 도메인에 대한 파사드 인터페이스
 * 다른 도메인에서 Post 도메인에 접근할 때 사용하는 인터페이스입니다.
 */
public interface PostFacade {
    /**
     * ID로 게시글을 조회합니다.
     * 
     * @param id 게시글 ID
     * @return 조회된 게시글 엔티티
     * @throws com.pyeon.global.exception.CustomException 게시글이 존재하지 않을 경우
     */
    Post getPostById(Long id);
    
    /**
     * 게시글의 존재 여부를 확인합니다.
     * 
     * @param id 게시글 ID
     * @return 게시글 존재 여부
     */
    boolean isPostExists(Long id);
    
    /**
     * 게시글의 조회수를 증가시킵니다.
     * 
     * @param id 게시글 ID
     */
    void incrementViewCount(Long id);
} 