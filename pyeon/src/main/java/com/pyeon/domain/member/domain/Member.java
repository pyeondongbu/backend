package com.pyeon.domain.member.domain;

import com.pyeon.domain.auth.domain.Authority;
import com.pyeon.domain.post.domain.Post;
import com.pyeon.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;
import org.hibernate.annotations.SQLDelete;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "is_active = true")
@SQLDelete(sql = "UPDATE member SET is_active = false WHERE id = ?")
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String nickname;

    @Column(length = 2000)
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Authority authority;

    @Column(nullable = false)
    private boolean isActive;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @Builder
    public Member(String email, String nickname, String profileImageUrl) {
        this.email = email;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.authority = Authority.ROLE_USER;
        this.isActive = true;
    }

    public void updateProfile(String nickname, String profileImageUrl) {
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }

    public void promoteToAdmin() {
        this.authority = Authority.ROLE_ADMIN;
    }

    // 관리자인지 확인
    public boolean isAdmin() {
        return this.authority == Authority.ROLE_ADMIN;
    }

    // 본인 확인
    public boolean isOwner(String email) {
        return this.email.equals(email);
    }

    // 계정 비활성화 (정지)
    public void delete() {
        this.isActive = false;
        this.posts.forEach(Post::delete);
    }

    // 계정 활성화 (정지 해제)
    public void activate() {
        this.isActive = true;
    }
}