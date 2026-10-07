package com.identityhub.service;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KeycloakAdminService {

    private final RealmResource realm;

    public KeycloakAdminService() {
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl("http://keycloak:8080")
                .realm("master")
                .grantType(OAuth2Constants.PASSWORD)
                .clientId("admin-cli")
                .username("admin")
                .password("admin_dev")
                .build();

        this.realm = keycloak.realm("identityhub");
    }

    public List<UserRepresentation> getUsers() {
        return realm.users().list();
    }

    public List<RoleRepresentation> getRoles() {
        return realm.roles().list();
    }

    public List<RoleRepresentation> getUserRoles(String userId) {
        return realm.users()
                .get(userId)
                .roles()
                .realmLevel()
                .listEffective();
    }

    public void assignRole(String userId, String roleName) {
        RoleRepresentation role = realm.roles()
                .get(roleName)
                .toRepresentation();

        realm.users()
                .get(userId)
                .roles()
                .realmLevel()
                .add(List.of(role));
    }

    public void removeRole(String userId, String roleName) {
        RoleRepresentation role = realm.roles()
                .get(roleName)
                .toRepresentation();

        realm.users()
                .get(userId)
                .roles()
                .realmLevel()
                .remove(List.of(role));
    }
}
