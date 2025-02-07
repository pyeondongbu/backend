package com.pyeon.domain.post.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostCreateRequest {
    @NotBlank(message = "제목은 필수입니다")
    private String title;

    @NotBlank(message = "내용은 필수입니다")
    private String content;

    @Builder(access = AccessLevel.PRIVATE)
    private PostCreateRequest(
            String title,
            String content
    ) {
        this.title = title;
        this.content = content;
    }

    public static PostCreateRequest of(
            String title,
            String content
    ) {
        return PostCreateRequest.builder()
                .title(title)
                .content(content)
                .build();
    }
} 