package com.pyeon.domain.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberUpdateRequest {
    @NotBlank(message = "닉네임은 필수입니다")
    private String nickname;
    
    private String profileImageUrl;

    @Builder(access = AccessLevel.PRIVATE)
    private MemberUpdateRequest(
            final String nickname,
            final String profileImageUrl
    ) {
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }

    public static MemberUpdateRequest of(
            final String nickname,
            final String profileImageUrl
    ) {
        return MemberUpdateRequest.builder()
                .nickname(nickname)
                .profileImageUrl(profileImageUrl)
                .build();
    }
} 