package com.pyeon.domain.post.presentation;

import com.pyeon.domain.auth.domain.UserPrincipal;
import com.pyeon.domain.post.dto.request.PostCreateRequest;
import com.pyeon.domain.post.dto.request.PostUpdateRequest;
import com.pyeon.domain.post.dto.response.PostResponse;
import com.pyeon.domain.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Long> createPost(
            @RequestBody @Valid PostCreateRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        Long postId = postService.createPost(request, principal.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(postId);
    }

    @GetMapping("/{postId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PostResponse> getPost(
            @PathVariable(name = "postId") Long postId
    ) {
        PostResponse response = postService.getPost(postId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<PostResponse>> getPosts(Pageable pageable) {
        return ResponseEntity.ok(postService.getPosts(pageable));
    }

    @PutMapping("/{postId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> updatePost(
            @PathVariable(name = "postId") Long postId,
            @RequestBody @Valid PostUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        postService.updatePost(postId, request, principal.getId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deletePost(
            @PathVariable(name = "postId") Long postId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        postService.deletePost(postId, principal.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{postId}/like")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> likePost(
            @PathVariable(name = "postId") Long postId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        postService.likePost(postId, principal.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{postId}/like")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Boolean> hasLiked(
            @PathVariable(name = "postId") Long postId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(postService.hasLiked(postId, principal.getId()));
    }
}
