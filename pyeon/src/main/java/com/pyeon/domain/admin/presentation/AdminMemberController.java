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

    @PostMapping("/{memberId}/suspend")
    public ResponseEntity<Void> suspendMember(@PathVariable Long memberId) {
        adminMemberService.suspendMember(memberId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{memberId}/ban")
    public ResponseEntity<Void> banMember(@PathVariable Long memberId) {
        adminMemberService.banMember(memberId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{memberId}/activate")
    public ResponseEntity<Void> activateMember(@PathVariable Long memberId) {
        adminMemberService.activateMember(memberId);
        return ResponseEntity.ok().build();
    }
} 