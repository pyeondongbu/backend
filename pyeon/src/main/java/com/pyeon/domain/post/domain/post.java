package com.pyeon.domain.post.domain;

import com.pyeon.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "active = true")
@SQLDelete(sql = "UPDATE post SET active = false WHERE id = ?")
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false, length = 50)
    private String author;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Category category;

    @Column(columnDefinition = "integer default 0", nullable = false)
    private int viewCount;

    @Column(columnDefinition = "integer default 0", nullable = false)
    private int recommendCount;

    @Column(columnDefinition = "boolean default true", nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @Builder
    public Post(
            String title,
            String content,
            String author,
            Category category
    ) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.category = category;
    }

    public void update(
            String title,
            String content,
            Category category
    ) {
        this.title = title;
        this.content = content;
        this.category = category;
    }

    public void increaseViewCount() {
        this.viewCount++;
    }

    public void increaseRecommendCount() {
        this.recommendCount++;
    }
}
