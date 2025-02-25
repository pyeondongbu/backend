package com.pyeon.domain.post.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentCreateRequest {
    @NotBlank(message = "내용은 필수입니다")
    private String content;

    @Builder
    private CommentCreateRequest(String content) {
        this.content = content;
    }
} 