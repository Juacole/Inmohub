package com.inmohub.lead.service.domain.model;

import com.inmohub.lead.service.domain.abstractions.AuditableEntity;
import com.inmohub.lead.service.domain.abstractions.DomainException;
import com.inmohub.lead.service.domain.model.enums.LeadSource;
import com.inmohub.lead.service.domain.model.enums.LeadStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class Lead extends AuditableEntity<UUID> {
    private String name;
    private String email;
    private String phone;
    private String message;
    private LeadSource source;
    private UUID propertyId;
    private LeadStatus status;

    // Constructor inicial
    public Lead(String name, String email, String phone, String message, LeadSource source, UUID propertyId) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.message = message;
        this.source = source;
        this.propertyId = propertyId;
        this.status = LeadStatus.NEW;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
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

    public String getEmail() {
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
