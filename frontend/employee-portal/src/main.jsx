import React, {useEffect, useState} from 'react';
import {createRoot} from 'react-dom/client';
import Keycloak from 'keycloak-js';
import './style.css';

const keycloak = new Keycloak({url:'http://localhost:8080', realm:'identityhub', clientId:'employee-portal'});

function App(){
  const [ready,setReady]=useState(false), [user,setUser]=useState(null), [admin,setAdmin]=useState(null), [events,setEvents]=useState([]), [error,setError]=useState('');
  useEffect(()=>{keycloak.init({onLoad:'login-required',pkceMethod:'S256',checkLoginIframe:false}).then(async authenticated=>{if(!authenticated)return; setReady(true); setUser(await get('/api/me'));}).catch(e=>setError(e.message));},[]);
  async function get(path){const r=await fetch(`http://localhost:8081${path}`,{headers:{Authorization:`Bearer ${keycloak.token}`}}); if(!r.ok) throw new Error(`${r.status} ${r.statusText}`); return r.json();}
  async function loadAdmin(){try{setAdmin(await get('/api/admin/ping'));setEvents(await get('/api/audit/events'));setError('');}catch(e){setError(e.message);}}
  if(!ready) return <main><h1>IdentityHub</h1><p>Connecting to the identity provider…</p>{error&&<pre>{error}</pre>}</main>;
  return <main><header><div><h1>IdentityHub</h1><p>Employee Portal</p></div><button onClick={()=>keycloak.logout()}>Sign out</button></header>
    <section className="card"><h2>Signed in</h2><p><b>{user?.username}</b></p><p>{user?.authorities?.join(' · ')}</p></section>
    <div className="grid"><section className="card"><h2>Protected API</h2><button onClick={()=>get('/api/profile').then(setAdmin).catch(e=>setError(e.message))}>Test profile access</button></section><section className="card"><h2>Admin access</h2><button onClick={loadAdmin}>Test admin endpoint</button>{admin&&<pre>{JSON.stringify(admin,null,2)}</pre>}</section></div>
    <section className="card"><h2>Recent audit events</h2><button onClick={()=>get('/api/audit/events').then(setEvents).catch(e=>setError(e.message))}>Refresh</button>{events.map(e=><div className="event" key={e.id}><b>{e.action}</b> · {e.actor} · {e.result}<small>{e.createdAt}</small></div>)}</section>{error&&<div className="error">{error}</div>}</main>
}
createRoot(document.getElementById('root')).render(<App/>);
