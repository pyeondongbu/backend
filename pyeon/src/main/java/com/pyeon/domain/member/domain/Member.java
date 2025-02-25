package com.pyeon.domain.member.domain;

import com.pyeon.domain.auth.domain.Authority;
import com.pyeon.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;
import org.hibernate.annotations.SQLDelete;

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

    // 특정 권한을 가지고 있는지 확인
    public boolean hasRole(Authority authority) {
        return this.authority == authority;
    }

    // 관리자인지 확인
    public boolean isAdmin() {
        return this.authority == Authority.ROLE_ADMIN;
    }

    // 본인 확인
    public boolean isOwner(String email) {
        return this.email.equals(email);
    }

    public void delete() {
        this.isActive = false;
    }
}