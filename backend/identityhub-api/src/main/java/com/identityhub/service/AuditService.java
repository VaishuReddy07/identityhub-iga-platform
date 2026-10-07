package com.identityhub.service;

import com.identityhub.model.AuditEvent;
import com.identityhub.repo.AuditEventRepository;
import org.springframework.stereotype.Service;

@Service
public class AuditService {
    private final AuditEventRepository repository;
    public AuditService(AuditEventRepository repository) { this.repository = repository; }

    public AuditEvent record(String actor, String action, String target, String result, String correlationId) {
        return repository.save(new AuditEvent(actor, action, target, result, correlationId));
    }
}
