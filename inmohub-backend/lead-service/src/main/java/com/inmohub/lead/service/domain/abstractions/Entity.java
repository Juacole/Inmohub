package com.inmohub.lead.service.domain.abstractions;

import java.util.UUID;

/**
 * Entidad base del dominio con identidad tipada por UUID.
 * Proporciona un identificador comun para todas las entidades del sistema.
 */
public abstract class Entity<TId extends UUID> {
    public TId id;

    public TId getId() {
        return id;
    }
}
