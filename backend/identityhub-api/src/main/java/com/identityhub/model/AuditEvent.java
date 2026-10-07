package com.identityhub.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_events")
public class AuditEvent {
    @Id
    private UUID id;
    private String actor;
    private String action;
    private String target;
    private String result;
    private Instant createdAt;
    private String correlationId;

    protected AuditEvent() {}

    public AuditEvent(String actor, String action, String target, String result, String correlationId) {
        this.id = UUID.randomUUID();
        this.actor = actor;
        this.action = action;
        this.target = target;
        this.result = result;
        this.createdAt = Instant.now();
        this.correlationId = correlationId;
    }

    public UUID getId() { return id; }
    public String getActor() { return actor; }
    public String getAction() { return action; }
    public String getTarget() { return target; }
    public String getResult() { return result; }
    public Instant getCreatedAt() { return createdAt; }
    public String getCorrelationId() { return correlationId; }
}
