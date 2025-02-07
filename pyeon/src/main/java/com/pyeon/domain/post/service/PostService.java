package com.pyeon.domain.post.service;

import com.pyeon.domain.post.dto.request.PostCreateRequest;
import com.pyeon.domain.post.dto.request.PostUpdateRequest;
import com.pyeon.domain.post.dto.response.PostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {
    Long createPost(PostCreateRequest request, String email);
    PostResponse getPost(Long id);
    Page<PostResponse> getPosts(Pageable pageable);
    void updatePost(Long id, PostUpdateRequest request, String email);

    boolean hasLiked(Long postId, String email);

    void likePost(Long postId, String email);

    void deletePost(Long id, String email);

}
