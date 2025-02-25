package com.pyeon.domain.post.dto.request;

import com.pyeon.domain.post.domain.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostUpdateRequest {
    @NotBlank(message = "제목은 필수입니다")
    private String title;

    @NotBlank(message = "내용은 필수입니다")
    private String content;

    @NotNull(message = "카테고리는 필수입니다")
    private Category category;

    @Builder(access = AccessLevel.PRIVATE)
    private PostUpdateRequest(
            final String title,
            final String content,
            final Category category
    ) {
        this.title = title;
        this.content = content;
        this.category = category;
    }

    public static PostUpdateRequest of(
            final String title,
            final String content,
            final Category category
    ) {
        return PostUpdateRequest.builder()
                .title(title)
                .content(content)
                .category(category)
                .build();
    }
} 