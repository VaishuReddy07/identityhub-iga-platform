package com.identityhub.controller;

import com.identityhub.service.KeycloakAdminService;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('IAM_ADMIN')")
public class UserAdminController {

    private final KeycloakAdminService keycloakAdminService;

    public UserAdminController(KeycloakAdminService keycloakAdminService) {
        this.keycloakAdminService = keycloakAdminService;
    }

    @GetMapping
    public List<UserRepresentation> getUsers() {
        return keycloakAdminService.getUsers();
    }
}
