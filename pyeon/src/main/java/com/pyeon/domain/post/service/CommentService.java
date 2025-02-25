package com.pyeon.domain.post.service;

import com.pyeon.domain.post.dto.request.CommentCreateRequest;

public interface CommentService {
    Long createComment(Long postId, CommentCreateRequest request, Long memberId);
    
    void updateComment(Long commentId, CommentCreateRequest request, Long memberId);
    
    void deleteComment(Long commentId, Long memberId);
} 