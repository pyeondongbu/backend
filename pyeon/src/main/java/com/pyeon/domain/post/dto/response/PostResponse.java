package com.pyeon.domain.post.dto.response;

import com.pyeon.domain.post.domain.Post;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
            LocalDateTime modifiedAt
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
    }

    public static PostResponse from(Post post) {
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
                .build();
    }
} 