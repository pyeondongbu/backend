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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {
    private final PostService postService;

    @PostMapping
    public ResponseEntity<Long> createPost(
            @RequestBody @Valid PostCreateRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        Long postId = postService.createPost(request, principal.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(postId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPost(id));
    }

    @GetMapping
    public ResponseEntity<Page<PostResponse>> getPosts(Pageable pageable) {
        return ResponseEntity.ok(postService.getPosts(pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updatePost(
            @PathVariable Long id,
            @RequestBody @Valid PostUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        postService.updatePost(id, request, principal.getEmail());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        postService.deletePost(id, principal.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Void> likePost(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        postService.likePost(id, principal.getEmail());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/like")
    public ResponseEntity<Boolean> hasLiked(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(postService.hasLiked(id, principal.getEmail()));
    }
}
