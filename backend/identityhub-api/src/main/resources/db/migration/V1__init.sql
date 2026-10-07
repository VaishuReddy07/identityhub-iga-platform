create table audit_events (
  id uuid primary key,
  actor varchar(200) not null,
  action varchar(80) not null,
  target varchar(200),
  result varchar(40) not null,
  created_at timestamptz not null default now(),
  correlation_id varchar(100)
);
create index idx_audit_events_created_at on audit_events(created_at desc);
create index idx_audit_events_actor on audit_events(actor);
