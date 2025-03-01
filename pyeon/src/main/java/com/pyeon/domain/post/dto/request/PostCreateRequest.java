package com.pyeon.domain.post.dto.request;

import com.pyeon.domain.post.domain.enums.MainCategory;
import com.pyeon.domain.post.domain.enums.SubCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "메인 카테고리는 필수입니다")
    private MainCategory mainCategory;

    @NotNull(message = "서브 카테고리는 필수입니다")
    private SubCategory subCategory;

    @Builder(access = AccessLevel.PRIVATE)
    private PostCreateRequest(
            final String title,
            final String content,
            final MainCategory mainCategory,
            final SubCategory subCategory
    ) {
        this.title = title;
        this.content = content;
        this.mainCategory = mainCategory;
        this.subCategory = subCategory;
    }

    public static PostCreateRequest of(
            final String title,
            final String content,
            final MainCategory mainCategory,
            final SubCategory subCategory
    ) {
        return PostCreateRequest.builder()
                .title(title)
                .content(content)
                .mainCategory(mainCategory)
                .subCategory(subCategory)
                .build();
    }
} 