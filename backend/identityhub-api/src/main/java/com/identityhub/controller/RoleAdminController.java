package com.identityhub.controller;

import com.identityhub.service.KeycloakAdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/roles")
@PreAuthorize("hasRole('IAM_ADMIN')")
public class RoleAdminController {

    private final KeycloakAdminService keycloakAdminService;

    public RoleAdminController(KeycloakAdminService keycloakAdminService) {
        this.keycloakAdminService = keycloakAdminService;
    }

    @GetMapping
    public List<?> getRoles() {
        return keycloakAdminService.getRoles();
    }

    @GetMapping("/users/{userId}")
    public List<?> getUserRoles(@PathVariable String userId) {
        return keycloakAdminService.getUserRoles(userId);
    }

    @PostMapping("/users/{userId}/assign")
    public ResponseEntity<?> assignRole(
            @PathVariable String userId,
            @RequestBody Map<String, String> request) {

        keycloakAdminService.assignRole(userId, request.get("roleName"));

        return ResponseEntity.ok(Map.of(
                "message", "Role assigned successfully",
                "userId", userId,
                "roleName", request.get("roleName")
        ));
    }

    @DeleteMapping("/users/{userId}/remove/{roleName}")
    public ResponseEntity<?> removeRole(
            @PathVariable String userId,
            @PathVariable String roleName) {

        keycloakAdminService.removeRole(userId, roleName);

        return ResponseEntity.ok(Map.of(
                "message", "Role removed successfully",
                "userId", userId,
                "roleName", roleName
        ));
    }
}
