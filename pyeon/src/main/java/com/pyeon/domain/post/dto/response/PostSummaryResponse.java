package com.pyeon.domain.post.dto.response;

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
    private String authorName;
    private long viewCount;
    private long likeCount;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    @Builder(access = AccessLevel.PRIVATE)
    private PostSummaryResponse(Long id, String title, String authorName, 
                              long viewCount, long likeCount, 
                              LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.title = title;
        this.authorName = authorName;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static PostSummaryResponse from(Post post) {
        return PostSummaryResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .authorName(post.getMember().getNickname())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .createdAt(post.getCreatedAt())
                .modifiedAt(post.getModifiedAt())
                .build();
    }
} 