package com.pyeon.domain.member.presentation;

import com.pyeon.domain.auth.domain.UserPrincipal;
import com.pyeon.domain.member.dto.request.MemberUpdateRequest;
import com.pyeon.domain.member.dto.response.MemberResponse;
import com.pyeon.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MemberResponse> getMyInfo(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(memberService.getMember(principal.getId()));
    }

    @GetMapping("/{memberId}")
    @PreAuthorize("@memberAuthChecker.canAccess(#memberId, principal)")
    public ResponseEntity<MemberResponse> getMember(
            @PathVariable Long memberId
    ) {
        return ResponseEntity.ok(memberService.getMember(memberId));
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> updateMyInfo(
            @RequestBody @Valid MemberUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        memberService.updateMember(principal.getId(), request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteMyAccount(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        memberService.deleteMember(principal.getId());
        return ResponseEntity.ok().build();
    }
} 