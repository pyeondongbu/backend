package com.pyeon.domain.post.domain;

import com.pyeon.domain.member.domain.Member;
import com.pyeon.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "is_active = true")
@SQLDelete(sql = "UPDATE comment SET is_active = false WHERE id = ?")
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

    @Column(nullable = false)
    private boolean isActive;

    @Builder
    public Comment(
            String content,
            Member member,
            Post post
    ) {
        this.content = content;
        this.member = member;
        this.post = post;
        this.isActive = true;
    }

    public void update(String content) {
        this.content = content;
    }

    public boolean isAuthor(String email) {
        return member.getEmail().equals(email);
    }

    public void delete() {
        this.isActive = false;
    }
}
