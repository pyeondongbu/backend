package com.pyeon.domain.member.domain;

import com.pyeon.domain.auth.domain.Authority;
import com.pyeon.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String nickname;

    private String profileImage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Authority authority;

    @Column(length = 2000)
    private String refreshToken;

    @Builder
    public Member(String email, String nickname, String profileImage) {
        this.email = email;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.authority = Authority.ROLE_USER;  // 기본 권한은 USER
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateProfile(String nickname, String profileImage) {
        this.nickname = nickname;
        this.profileImage = profileImage;
    }

    public void promoteToAdmin() {
        this.authority = Authority.ROLE_ADMIN;
    }
}