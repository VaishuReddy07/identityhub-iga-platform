import os
from typing import List

import requests
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field

app = FastAPI(title="IdentityHub Provisioning API", version="0.1.0")

KEYCLOAK_URL = os.getenv("KEYCLOAK_URL", "http://localhost:8080").rstrip("/")
KEYCLOAK_REALM = os.getenv("KEYCLOAK_REALM", "identityhub")
KEYCLOAK_ADMIN = os.getenv("KEYCLOAK_ADMIN", "admin")
KEYCLOAK_ADMIN_PASSWORD = os.getenv("KEYCLOAK_ADMIN_PASSWORD", "admin_dev")


class UserRequest(BaseModel):
    username: str = Field(min_length=3, max_length=80)
    email: str
    first_name: str = ""
    last_name: str = ""
    password: str = Field(min_length=8)
    roles: List[str] = Field(default_factory=lambda: ["EMPLOYEE"])


def admin_token() -> str:
    response = requests.post(
        f"{KEYCLOAK_URL}/realms/master/protocol/openid-connect/token",
        data={
            "grant_type": "password",
            "client_id": "admin-cli",
            "username": KEYCLOAK_ADMIN,
            "password": KEYCLOAK_ADMIN_PASSWORD,
        },
        timeout=10,
    )
    if response.status_code != 200:
        raise HTTPException(status_code=502, detail="Could not authenticate with Keycloak")
    return response.json()["access_token"]


def role_ids(token: str, roles: List[str]) -> list[dict]:
    headers = {"Authorization": f"Bearer {token}"}
    result = []
    for role in roles:
        response = requests.get(
            f"{KEYCLOAK_URL}/admin/realms/{KEYCLOAK_REALM}/roles/{role}",
            headers=headers,
            timeout=10,
        )
        if response.status_code != 200:
            raise HTTPException(status_code=400, detail=f"Unknown realm role: {role}")
        data = response.json()
        result.append({"id": data["id"], "name": data["name"]})
    return result


@app.get("/health")
def health():
    return {"status": "UP"}


@app.post("/provision/users", status_code=201)
def provision_user(user: UserRequest):
    token = admin_token()
    headers = {"Authorization": f"Bearer {token}", "Content-Type": "application/json"}

    payload = {
        "username": user.username,
        "email": user.email,
        "firstName": user.first_name,
        "lastName": user.last_name,
        "enabled": True,
        "emailVerified": False,
        "credentials": [
            {"type": "password", "value": user.password, "temporary": True}
        ],
    }

    response = requests.post(
        f"{KEYCLOAK_URL}/admin/realms/{KEYCLOAK_REALM}/users",
        headers=headers,
        json=payload,
        timeout=10,
    )
    if response.status_code != 201:
        detail = response.text[:500]
        raise HTTPException(status_code=response.status_code, detail=detail)

    location = response.headers.get("Location", "")
    user_id = location.rstrip("/").split("/")[-1]

    for role in role_ids(token, user.roles):
        role_response = requests.post(
            f"{KEYCLOAK_URL}/admin/realms/{KEYCLOAK_REALM}/users/{user_id}/role-mappings/realm",
            headers=headers,
            json=[role],
            timeout=10,
        )
        if role_response.status_code not in (204, 200):
            raise HTTPException(status_code=502, detail=f"Could not assign role {role['name']}")

    return {"id": user_id, "username": user.username, "roles": user.roles}
