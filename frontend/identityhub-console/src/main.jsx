import React, { useEffect, useState } from 'react';
import { createRoot } from 'react-dom/client';
import Keycloak from 'keycloak-js';
import './style.css';

const keycloak = new Keycloak({
  url: 'http://localhost:8080',
  realm: 'identityhub',
  clientId: 'identityhub-console'
});

function App() {
  const [ready, setReady] = useState(false);
  const [user, setUser] = useState(null);
  const [profile, setProfile] = useState(null);
  const [admin, setAdmin] = useState(null);
  const [users, setUsers] = useState([]);
  const [roles, setRoles] = useState([]);
  const [events, setEvents] = useState([]);
  const [error, setError] = useState('');

  useEffect(() => {
    keycloak
      .init({
        onLoad: 'login-required',
        pkceMethod: 'S256',
        checkLoginIframe: false
      })
      .then(async authenticated => {
        if (!authenticated) {
          setError('Authentication failed');
          return;
        }

        setUser(await get('/api/me'));
        setReady(true);
      })
      .catch(error => {
        setError(error.message);
      });
  }, []);

  async function get(path) {
    const response = await fetch(`http://localhost:8081${path}`, {
      headers: {
        Authorization: `Bearer ${keycloak.token}`
      }
    });

    if (!response.ok) {
      throw new Error(`${response.status} ${response.statusText}`);
    }

    return response.json();
  }

  async function loadProfile() {
    try {
      setError('');
      const result = await get('/api/profile');
      setProfile(result);
      await loadEvents();
    } catch (error) {
      setError(error.message);
    }
  }

  async function loadAdmin() {
    try {
      setError('');
      const result = await get('/api/admin/ping');
      setAdmin(result);
      await loadEvents();
    } catch (error) {
      setError(error.message);
    }
  }

  async function loadUsers() {
    try {
      setError('');
      const result = await get('/api/admin/users');
      setUsers(result);
    } catch (error) {
      setError(error.message);
    }
  }

  async function loadRoles() {
    try {
      setError('');
      const result = await get('/api/admin/roles');
      setRoles(result);
    } catch (error) {
      setError(error.message);
    }
  }

  async function loadEvents() {
    try {
      const result = await get('/api/audit/events');
      setEvents(result);
    } catch (error) {
      setError(error.message);
    }
  }

  if (!ready) {
    return (
      <main>
        <h1>IdentityHub</h1>
        <p>Connecting to the identity provider...</p>
        {error && <div className="error">{error}</div>}
      </main>
    );
  }

  return (
    <main>
      <header>
        <div>
          <h1>IdentityHub</h1>
          <p>Identity & Access Management Console</p>
        </div>

        <button onClick={() => keycloak.logout()}>
          Sign out
        </button>
      </header>

      <section className="card">
        <h2>Signed in</h2>

        <p>
          <b>{user?.username}</b>
        </p>

        <p>
          {user?.authorities?.join(' · ')}
        </p>
      </section>

      <div className="grid">
        <section className="card">
          <h2>Protected API</h2>

          <button onClick={loadProfile}>
            Test profile access
          </button>

          {profile && (
            <pre>
              {JSON.stringify(profile, null, 2)}
            </pre>
          )}
        </section>

        <section className="card">
          <h2>Admin access</h2>

          <button onClick={loadAdmin}>
            Test admin endpoint
          </button>

          {admin && (
            <pre>
              {JSON.stringify(admin, null, 2)}
            </pre>
          )}
        </section>
      </div>

      {user?.authorities?.includes('ROLE_IAM_ADMIN') && (
        <section className="card">
          <h2>User Management</h2>

          <button onClick={loadUsers}>
            Load users
          </button>

          {users.length > 0 && (
            <div className="events">
              {users.map(currentUser => (
                <div className="event" key={currentUser.id}>
                  <b>{currentUser.username}</b>
                  {' · '}
                  {currentUser.email || 'No email'}
                  {' · '}
                  {currentUser.enabled ? 'Enabled' : 'Disabled'}
                </div>
              ))}
            </div>
          )}
        </section>
      )}

      {user?.authorities?.includes('ROLE_IAM_ADMIN') && (
        <section className="card">
          <h2>Role Management</h2>

          <button onClick={loadRoles}>
            Load roles
          </button>

          {roles.length > 0 && (
            <div className="events">
              {roles.map(role => (
                <div className="event" key={role.id}>
                  <b>{role.name}</b>
                  {' · '}
                  {role.description || 'No description'}
                </div>
              ))}
            </div>
          )}
        </section>
      )}

      <section className="card">
        <h2>Recent audit events</h2>

        <button onClick={loadEvents}>
          Refresh
        </button>

        <div className="events">
          {events.map(event => (
            <div className="event" key={event.id}>
              <b>{event.action}</b>
              {' · '}
              {event.actor}
              {' · '}
              {event.result}

              <small>
                {event.createdAt}
              </small>
            </div>
          ))}
        </div>
      </section>

      {error && (
        <div className="error">
          {error}
        </div>
      )}
    </main>
  );
}

createRoot(document.getElementById('root')).render(<App />);
