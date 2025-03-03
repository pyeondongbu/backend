package com.pyeon.domain.post.facade;

import com.pyeon.domain.post.dao.PostRepository;
import com.pyeon.domain.post.domain.Post;
import com.pyeon.global.exception.CustomException;
import com.pyeon.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Post 도메인에 대한 파사드 구현체
 */
@Component
@RequiredArgsConstructor
public class PostFacadeImpl implements PostFacade {
    private final PostRepository postRepository;
    
    @Override
    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
    }
    
    @Override
    public boolean isPostExists(Long id) {
        return postRepository.existsById(id);
    }
    
    @Override
    @Transactional
    public void incrementViewCount(Long id) {
        Post post = getPostById(id);
        post.incrementViewCount();
        postRepository.save(post);
    }
} 