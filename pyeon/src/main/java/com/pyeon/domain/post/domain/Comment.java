package com.pyeon.domain.post.domain;

import com.pyeon.domain.member.domain.Member;
import com.pyeon.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Builder
    public Comment(
            String content,
            Member member,
            Post post
    ) {
        this.content = content;
        this.member = member;
        this.post = post;
    }

    public void update(String content) {
        this.content = content;
    }

    public boolean isAuthor(String email) {
        return member.getEmail().equals(email);
    }
}
