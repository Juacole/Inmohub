package com.inmohub.lead.service.domain.abstractions;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad base que anade campos de auditoria (creacion y ultima modificacion)
 * a las entidades del dominio. Las subclases heredan la identidad de {@link Entity}.
 */
public abstract class AuditableEntity<TId extends UUID> extends Entity<TId> {
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;

    protected void marksAsUpdated() {
        this.updatedAt = LocalDateTime.now();
    }
}
