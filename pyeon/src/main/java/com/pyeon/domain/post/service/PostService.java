package com.pyeon.domain.post.service;

import com.pyeon.domain.post.domain.enums.MainCategory;
import com.pyeon.domain.post.domain.enums.SubCategory;
import com.pyeon.domain.post.dto.request.PostCreateRequest;
import com.pyeon.domain.post.dto.request.PostUpdateRequest;
import com.pyeon.domain.post.dto.response.PostResponse;
import com.pyeon.domain.post.dto.response.PostSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {
    Long createPost(PostCreateRequest request, Long memberId);
    
    PostResponse getPost(Long id, Long memberId);

    public Page<PostResponse> getPosts(
            MainCategory mainCategory,
            SubCategory subCategory,
            String searchText,
            boolean onlyPopular,
            Pageable pageable
    );

    public Page<PostSummaryResponse> getPostsSummary(
            MainCategory mainCategory,
            SubCategory subCategory,
            String searchText,
            boolean onlyPopular,
            Pageable pageable
    );

    Page<PostResponse> getPostsByMemberId(Long memberId, Pageable pageable);
    
    Page<PostSummaryResponse> getPostsSummaryByMemberId(Long memberId, Pageable pageable);
    
    void updatePost(Long postId, PostUpdateRequest request, Long memberId);
    
    void deletePost(Long postId, Long memberId);
    
    void likePost(Long postId, Long memberId);

    /**
     * 게시글 조회수를 증가시킵니다.
     * 
     * @param postId 조회수를 증가시킬 게시글 ID
     */
    void incrementViewCount(Long postId);
}
