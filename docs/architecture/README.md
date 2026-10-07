# Architecture notes

The first runnable slice keeps the authentication path simple:

```text
Browser
  ├── IdentityHub Console :3000
  └── Employee Portal     :3001
             │
             ▼
       Keycloak :8080
          OIDC / SSO
             │ JWT
             ▼
       Spring Boot :8081
             │
             ▼
       PostgreSQL :5432
```

OpenLDAP is included as a separate local directory lab. It is not automatically federated into Keycloak in this first slice.

The Python provisioning service is a development helper. It talks to the Keycloak Admin REST API and should be hardened before production use.
