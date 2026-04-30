package com.inmohub.lead.service.domain.model;

import com.inmohub.lead.service.domain.abstractions.AuditableEntity;
import com.inmohub.lead.service.domain.abstractions.DomainException;
import com.inmohub.lead.service.domain.model.enums.LeadSource;
import com.inmohub.lead.service.domain.model.enums.LeadStatus;
import com.inmohub.lead.service.domain.valueobjetcs.Email;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Lead extends AuditableEntity<UUID> {
    private final String name;
    private final Email email;
    private final String phone;
    private final String message;
    private final LeadSource source;
    private final UUID propertyId;
    private LeadStatus status;

    // Constructor inicial
    private Lead(String name, Email email, String phone, String message, LeadSource source, UUID propertyId) {
        Objects.requireNonNull(email, "El email es obligatorio.");
        Objects.requireNonNull(source, "La fuente del lead (source) es obligatoria.");
        Objects.requireNonNull(propertyId, "El ID de la propiedad es obligatorio.");

        if (name == null || name.isBlank()) {
            throw new DomainException("El nombre del lead no puede estar vacío.");
        }

        if (phone == null || phone.isBlank()) {
            throw new DomainException("El teléfono es obligatorio para contactar al lead.");
        }

        this.id = UUID.randomUUID();
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.message = (message == null) ? "" : message;
        this.source = source;
        this.propertyId = propertyId;
        this.status = LeadStatus.NEW;

        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    public static Lead create(
            String name,
            Email email,
            String phone,
            String message,
            LeadSource source,
            UUID propertyId
    ) {
        return new Lead(name, email, phone, message, source, propertyId);
    }

    public static Lead reconstitute(
            UUID id,
            String name,
            Email email,
            String phone,
            String message,
            LeadSource source,
            UUID propertyId,
            LeadStatus status
    ) {
        Lead lead = new Lead(name, email, phone, message, source, propertyId);
        lead.id = id;
        lead.status = status;
        return lead;
    }

    public void contactLead() {
        if (this.status == LeadStatus.CLOSED || this.status == LeadStatus.LOST) {
            throw new DomainException("No se puede contactar un lead cerrado o perdido.");
        }
        this.status = LeadStatus.CONTACTED;
        marksAsUpdated();
    }

    public void closeLead() {
        this.status = LeadStatus.CLOSED;
        marksAsUpdated();
    }

    public String getName() {
        return name;
    }

    public Email getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getMessage() {
        return message;
    }

    public LeadSource getSource() {
        return source;
    }

    public UUID getPropertyId() {
        return propertyId;
    }

    public LeadStatus getStatus() {
        return status;
    }
}
