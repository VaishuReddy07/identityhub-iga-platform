package com.identityhub.repo;

import com.identityhub.model.AuditEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface AuditEventRepository extends JpaRepository<AuditEvent, UUID> {
    List<AuditEvent> findTop50ByOrderByCreatedAtDesc();
}
