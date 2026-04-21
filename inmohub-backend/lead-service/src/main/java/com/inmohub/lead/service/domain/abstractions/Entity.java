package com.inmohub.lead.service.domain.abstractions;

import java.util.UUID;

public abstract class Entity<TId extends UUID> {
    public TId id;

    public TId getId() {
        return id;
    }
}
