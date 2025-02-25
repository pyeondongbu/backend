package com.pyeon.domain.post.service;

import com.pyeon.domain.post.domain.Category;
import com.pyeon.domain.post.dto.request.PostCreateRequest;
import com.pyeon.domain.post.dto.request.PostUpdateRequest;
import com.pyeon.domain.post.dto.response.PostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {
    Long createPost(PostCreateRequest request, Long memberId);
    
    PostResponse getPost(Long id);

    public Page<PostResponse> getPosts(
            Category category,
            String searchText,
            boolean onlyPopular,
            Pageable pageable
    );
    void updatePost(Long postId, PostUpdateRequest request, Long memberId);
    
    void deletePost(Long postId, Long memberId);
    
    void likePost(Long postId, Long memberId);
    
    boolean hasLiked(Long postId, Long memberId);
}
