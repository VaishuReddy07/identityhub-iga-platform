package com.identityhub.controller;

import com.identityhub.service.KeycloakAdminService;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/roles")
@PreAuthorize("hasRole('IAM_ADMIN')")
public class RoleAdminController {

    private final KeycloakAdminService keycloakAdminService;

    public RoleAdminController(KeycloakAdminService keycloakAdminService) {
        this.keycloakAdminService = keycloakAdminService;
    }

    @GetMapping
    public List<RoleRepresentation> getRoles() {
        return keycloakAdminService.getRoles();
    }
}
