# IdentityHub quick demo

1. Start with `docker compose up --build`.
2. Open `http://localhost:3000`.
3. Log in with `iamadmin / Admin123!`.
4. Test profile access.
5. Test admin access and refresh the audit list.
6. Open `http://localhost:3001` in another tab.
7. Confirm the second app uses the same Keycloak session.
8. Log out and repeat with `vaishnavi / User123!`.
9. Confirm the employee account cannot call the admin endpoint.
10. Open `http://localhost:8081/swagger-ui.html` to inspect the API.
