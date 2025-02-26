package com.pyeon.domain.member.dto.response;

import com.pyeon.domain.auth.domain.Authority;
import com.pyeon.domain.member.domain.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberResponse {
    private Long id;
    private String email;
    private String nickname;
    private String profileImageUrl;
    private Authority authority;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    @Builder(access = AccessLevel.PRIVATE)
    private MemberResponse(
            Long id,
            String email,
            String nickname,
            String profileImageUrl,
            Authority authority,
            LocalDateTime createdAt,
            LocalDateTime modifiedAt
    ) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.authority = authority;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static MemberResponse from(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .profileImageUrl(member.getProfileImageUrl())
                .authority(member.getAuthority())
                .createdAt(member.getCreatedAt())
                .modifiedAt(member.getModifiedAt())
                .build();
    }
} 