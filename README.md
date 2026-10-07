# IdentityHub

A small, runnable IAM lab built with Keycloak, Spring Boot, React, PostgreSQL and OpenLDAP.

This project is intentionally straightforward. It demonstrates the core flow you would expect in an enterprise access-management internship project:

- central identity provider with Keycloak
- OAuth 2.0 / OpenID Connect login
- SSO between two React applications
- JWT validation in a Spring Boot resource server
- role-based access control
- audit events stored in PostgreSQL
- LDAP directory running locally for federation labs
- a small Python provisioning API for Keycloak

> This is a development/lab project. The sample credentials in this repository are not production credentials.

## Services

| Service | URL | Purpose |
|---|---|---|
| Keycloak | http://localhost:8080 | Identity provider and SSO |
| IdentityHub Console | http://localhost:3000 | IAM console demo |
| Employee Portal | http://localhost:3001 | Second application for SSO demo |
| Spring API | http://localhost:8081 | Protected resource server |
| Provisioning API | http://localhost:8000/docs | User provisioning lab |
| OpenLDAP | ldap://localhost:389 | LDAP lab |
| PostgreSQL | localhost:5432 | Application/audit database |

## Development accounts

| User | Password | Role |
|---|---|---|
| vaishnavi | User123! | EMPLOYEE |
| iamadmin | Admin123! | EMPLOYEE, IAM_ADMIN |
| security | Security123! | EMPLOYEE, SECURITY_ANALYST |

Keycloak administrator:

- username: `admin`
- password: `admin_dev`

These values are for local development only.

## Start

From this directory:

```bash
docker compose up --build
```

The first run downloads the container images and builds the Java, React and Python services.

To stop everything:

```bash
docker compose down
```

To stop everything and remove the local PostgreSQL volume:

```bash
docker compose down -v
```

Use `down -v` when you want a completely fresh Keycloak/application lab state.

## Login and SSO demo

1. Open `http://localhost:3000`.
2. Sign in as `iamadmin / Admin123!`.
3. Test **Profile access** and **Admin access**.
4. Open `http://localhost:3001` in another tab.
5. The second application should reuse the same Keycloak session instead of asking for credentials again.
6. Sign out from one application and test the login flow again.

Try `vaishnavi / User123!` as well. The employee account can access protected endpoints but should receive `403` from `/api/admin/ping`.

## API

Public health endpoint:

```text
GET http://localhost:8081/actuator/health
```

Authenticated endpoints:

```text
GET /api/me
GET /api/profile
```

Admin endpoint:

```text
GET /api/admin/ping
```

Audit endpoint:

```text
GET /api/audit/events
```

Swagger UI:

```text
http://localhost:8081/swagger-ui.html
```

## Provisioning API

The Python service exposes:

```text
GET  /health
POST /provision/users
```

Example body:

```json
{
  "username": "alice",
  "email": "alice@example.com",
  "first_name": "Alice",
  "last_name": "Martin",
  "password": "ChangeMe123!",
  "roles": ["EMPLOYEE"]
}
```

The service uses the local Keycloak admin account only for this development lab. A production version should use a dedicated service account, least-privilege permissions, secret management, audit trails and idempotent provisioning.

## LDAP

OpenLDAP starts with the `identityhub.local` directory and a sample user. Keycloak LDAP federation is deliberately kept as a separate lab step so the basic OIDC/SSO setup stays easy to run.

LDAP administrator:

```text
cn=admin,dc=identityhub,dc=local
password: ldap_dev
```

Sample user:

```text
uid=alice,ou=people,dc=identityhub,dc=local
password: Alice123!
```

## Project structure

```text
identityhub/
├── backend/
│   └── identityhub-api/
├── frontend/
│   ├── identityhub-console/
│   └── employee-portal/
├── infra/
│   └── keycloak/
├── ldap/
│   └── bootstrap/
├── python/
│   └── provisioning-service/
├── docs/
├── docker-compose.yml
└── README.md
```

## Next IAM labs

The repository is a foundation for extending the project with:

- Keycloak MFA/TOTP
- LDAP federation in Keycloak
- SAML 2.0 federation
- WS-Federation lab
- application registration and client management
- user lifecycle/provisioning and deprovisioning
- stronger audit/search features
- Prometheus/OpenTelemetry observability
- automated security and integration tests
- CI/CD
- Kubernetes/Helm deployment

Do not describe these as implemented features on a CV until you actually build and test them.
