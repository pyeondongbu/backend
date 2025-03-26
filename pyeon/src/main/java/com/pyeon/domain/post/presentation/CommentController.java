package com.pyeon.domain.post.presentation;

import com.pyeon.domain.auth.domain.UserPrincipal;
import com.pyeon.domain.post.dto.request.CommentCreateRequest;
import com.pyeon.domain.post.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts/{postId}/comments")
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    @PreAuthorize("hasRole('USER') and @postAuthChecker.canWrite(principal)")
    public ResponseEntity<Long> createComment(
            @PathVariable(name = "postId") Long postId,
            @RequestBody @Valid CommentCreateRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        Long commentId = commentService.createComment(postId, request, principal.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(commentId);
    }

    @PutMapping("/{commentId}")
    @PreAuthorize("hasRole('USER') and @commentAuthChecker.canModify(#id, principal)")
    public ResponseEntity<Void> updateComment(
            @PathVariable(name = "postId") Long postId,
            @PathVariable(name = "commentId") @P("id") Long commentId,
            @RequestBody @Valid CommentCreateRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        commentService.updateComment(commentId, request, principal.getId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasAnyRole('ADMIN') or (@commentAuthChecker.canModify(#id, principal))")
    public ResponseEntity<Void> deleteComment(
            @PathVariable(name = "postId") Long postId,
            @PathVariable(name = "commentId") @P("id") Long commentId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        commentService.deleteComment(commentId, principal.getId());
        return ResponseEntity.ok().build();
    }
} 