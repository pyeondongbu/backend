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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus status;

    public enum MemberStatus {
        ACTIVE,     // 정상
        SUSPENDED,  // 일시정지
        BANNED      // 영구정지
    }

    @Builder
    public Member(String email, String nickname, String profileImageUrl) {
        this.email = email;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.authority = Authority.ROLE_USER;
        this.isActive = true;
        this.status = MemberStatus.ACTIVE;
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

    // 관리자용 메서드
    public void suspend() {
        this.status = MemberStatus.SUSPENDED;
    }

    public void ban() {
        this.status = MemberStatus.BANNED;
        this.isActive = false;  // 영구정지는 계정 비활성화도 함께
    }

    public void activate() {
        this.status = MemberStatus.ACTIVE;
    }

    public boolean isBanned() {
        return this.status == MemberStatus.BANNED;
    }

    public boolean isSuspended() {
        return this.status == MemberStatus.SUSPENDED;
    }
}