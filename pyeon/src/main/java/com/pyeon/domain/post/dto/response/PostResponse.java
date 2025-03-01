package com.pyeon.domain.post.dto.response;

import com.pyeon.domain.post.domain.enums.MainCategory;
import com.pyeon.domain.post.domain.enums.SubCategory;
import com.pyeon.domain.post.domain.Post;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostResponse {
    private Long id;
    private String title;
    private String content;
    private String memberEmail;
    private String memberNickname;
    private long viewCount;
    private long likeCount;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private MainCategory mainCategory;
    private SubCategory subCategory;
    private List<CommentResponse> comments;
    private boolean hasLiked;

    @Builder(access = AccessLevel.PRIVATE)
    private PostResponse(
            Long id,
            String title,
            String content,
            String memberEmail,
            String memberNickname,
            long viewCount,
            long likeCount,
            LocalDateTime createdAt,
            LocalDateTime modifiedAt,
            MainCategory mainCategory,
            SubCategory subCategory,
            List<CommentResponse> comments,
            boolean hasLiked
    ) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.memberEmail = memberEmail;
        this.memberNickname = memberNickname;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.mainCategory = mainCategory;
        this.subCategory = subCategory;
        this.comments = comments;
        this.hasLiked = hasLiked;
    }

    public static PostResponse from(Post post, boolean hasLiked) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .memberEmail(post.getMember().getEmail())
                .memberNickname(post.getMember().getNickname())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .createdAt(post.getCreatedAt())
                .modifiedAt(post.getModifiedAt())
                .mainCategory(post.getMainCategory())
                .subCategory(post.getSubCategory())
                .comments(post.getComments().stream().map(CommentResponse::from).toList())
                .hasLiked(hasLiked)
                .build();
    }

    public static PostResponse from(Post post) {
        return from(post, false);
    }
} 