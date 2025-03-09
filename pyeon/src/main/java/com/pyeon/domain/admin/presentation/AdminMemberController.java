package com.pyeon.domain.admin.presentation;

import com.pyeon.domain.member.dto.response.MemberResponse;
import com.pyeon.domain.admin.service.AdminMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/members")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminMemberController {
    
    private final AdminMemberService adminMemberService;

    @GetMapping
    public ResponseEntity<Page<MemberResponse>> getAllMembers(Pageable pageable) {
        return ResponseEntity.ok(adminMemberService.getAllMembers(pageable));
    }

    @PostMapping("/{memberId}/deactivate")
    public ResponseEntity<Void> deactivateMember(
            @PathVariable(name = "memberId") Long memberId
    ) {
        adminMemberService.deactivateMember(memberId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{memberId}/activate")
    public ResponseEntity<Void> activateMember(
            @PathVariable(name = "memberId") Long memberId
    ) {
        adminMemberService.activateMember(memberId);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{memberId}/promote-to-admin")
    public ResponseEntity<Void> promoteToAdmin(@PathVariable Long memberId) {
        adminMemberService.promoteToAdmin(memberId);
        return ResponseEntity.ok().build();
    }
} 