package com.pyeon.domain.post.domain;

import com.pyeon.domain.member.domain.Member;
import com.pyeon.domain.post.domain.enums.MainCategory;
import com.pyeon.domain.post.domain.enums.SubCategory;
import com.pyeon.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "is_active = true")
@SQLDelete(sql = "UPDATE post SET is_active = false WHERE id = ?")
public class Post extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private long viewCount;

    @Column(nullable = false)
    private long likeCount;

    @Column(nullable = false)
    private boolean isActive;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MainCategory mainCategory;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SubCategory subCategory;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Post(String title, String content, Member member, MainCategory mainCategory, SubCategory subCategory) {
        this.title = title;
        this.content = content;
        this.member = member;
        this.mainCategory = mainCategory;
        this.subCategory = subCategory;
        this.viewCount = 0;
        this.likeCount = 0;
        this.isActive = true;
    }

    public void incrementViewCount() {
        this.viewCount++;
    }

    public void like() {
        this.likeCount++;
    }

    public void unlike() {
        this.likeCount = Math.max(0, this.likeCount - 1);  // 음수 방지
    }

    public void update(
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

    public boolean isWriter(Member member) {
        return this.member.getId().equals(member.getId());
    }

    public void delete() {
        this.isActive = false;
    }
}