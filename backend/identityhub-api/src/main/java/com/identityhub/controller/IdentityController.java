package com.identityhub.controller;

import com.identityhub.repo.AuditEventRepository;
import com.identityhub.service.AuditService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class IdentityController {
    private final AuditService auditService;
    private final AuditEventRepository auditEvents;

    public IdentityController(AuditService auditService, AuditEventRepository auditEvents) {
        this.auditService = auditService;
        this.auditEvents = auditEvents;
    }

    @GetMapping("/me")
    public Map<String, Object> me(Authentication authentication) {
        return Map.of(
            "username", authentication.getName(),
            "authorities", authentication.getAuthorities().stream().map(a -> a.getAuthority()).toList()
        );
    }

    @GetMapping("/profile")
    public Map<String, Object> profile(Authentication authentication) {
        auditService.record(authentication.getName(), "PROFILE_VIEWED", authentication.getName(), "SUCCESS", UUID.randomUUID().toString());
        return Map.of("message", "Profile access granted", "user", authentication.getName());
    }

    @GetMapping("/admin/ping")
    public Map<String, String> adminPing(Authentication authentication) {
        auditService.record(authentication.getName(), "ADMIN_ACCESS", "/api/admin/ping", "SUCCESS", UUID.randomUUID().toString());
        return Map.of("message", "Admin access granted", "user", authentication.getName());
    }

    @GetMapping("/audit/events")
    public ResponseEntity<?> auditEvents() {
        return ResponseEntity.ok(auditEvents.findTop50ByOrderByCreatedAtDesc());
    }
}
