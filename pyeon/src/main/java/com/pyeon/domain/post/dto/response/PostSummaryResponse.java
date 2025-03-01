package com.pyeon.domain.post.dto.response;

import com.pyeon.domain.post.domain.enums.MainCategory;
import com.pyeon.domain.post.domain.enums.SubCategory;
import com.pyeon.domain.post.domain.Post;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostSummaryResponse {
    private Long id;
    private String title;
    private String memberNickname;
    private long viewCount;
    private long likeCount;
    private long commentCount;
    private MainCategory mainCategory;
    private SubCategory subCategory;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    @Builder(access = AccessLevel.PRIVATE)
    private PostSummaryResponse(
            Long id, String title,
            String memberNickname,
            long viewCount,
            long likeCount,
            long commentCount,
            MainCategory mainCategory,
            SubCategory subCategory,
            LocalDateTime createdAt,
            LocalDateTime modifiedAt
    ) {
        this.id = id;
        this.title = title;
        this.memberNickname = memberNickname;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.mainCategory = mainCategory;
        this.subCategory = subCategory;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static PostSummaryResponse from(Post post) {
        return PostSummaryResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .memberNickname(post.getMember().getNickname())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .commentCount(post.getComments().size())
                .mainCategory(post.getMainCategory())
                .subCategory(post.getSubCategory())
                .createdAt(post.getCreatedAt())
                .modifiedAt(post.getModifiedAt())
                .build();
    }
} 