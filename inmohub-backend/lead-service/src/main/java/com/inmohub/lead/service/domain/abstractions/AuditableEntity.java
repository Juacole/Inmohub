package com.inmohub.lead.service.domain.abstractions;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class AuditableEntity<TId extends UUID> extends Entity<TId> {
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;

    protected void marksAsUpdated() {
        this.updatedAt = LocalDateTime.now();
    }
}
