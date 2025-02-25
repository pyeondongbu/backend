package com.pyeon.domain.post.dto.response;

import com.pyeon.domain.post.domain.Comment;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentResponse {
    private Long id;
    private String content;
    private String memberEmail;
    private String memberNickname;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    @Builder
    private CommentResponse(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.memberEmail = comment.getMember().getEmail();
        this.memberNickname = comment.getMember().getNickname();
        this.createdAt = comment.getCreatedAt();
        this.modifiedAt = comment.getModifiedAt();
    }

    public static CommentResponse from(Comment comment) {
        return new CommentResponse(comment);
    }
} 