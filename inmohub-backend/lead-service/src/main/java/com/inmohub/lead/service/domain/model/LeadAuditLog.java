package com.inmohub.lead.service.domain.model;

import com.inmohub.lead.service.domain.model.enums.LeadStatus;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Registro de auditoria que documenta los cambios de estado de un lead.
 * Almacena el estado anterior, el nuevo estado y quien realizo el cambio.
 */
public class LeadAuditLog {
    private final UUID id;
    private final UUID leadId;
    private final LeadStatus previousStatus;
    private final LeadStatus newStatus;
    private final String actionDescription;
    private final LocalDateTime changedAt;
    private final UUID changedByUserId;

    private LeadAuditLog(
            UUID leadId,
            LeadStatus previousStatus,
            LeadStatus newStatus,
            String actionDescription,
            UUID changedByUserId
    ) {
        this.id = UUID.randomUUID();
        this.leadId = Objects.requireNonNull(leadId, "El ID del lead es obligatorio.");
        this.newStatus = Objects.requireNonNull(newStatus, "El nuevo estado es obligatorio.");
        this.previousStatus = previousStatus; // Puede ser null
        this.actionDescription = (actionDescription == null) ? "" : actionDescription;
        this.changedAt = LocalDateTime.now();
        this.changedByUserId = changedByUserId; // Puede ser null si el cambio es por el sistema
    }


    public static LeadAuditLog create(
            UUID leadId,
            LeadStatus previousStatus,
            LeadStatus newStatus,
            String actionDescription,
            UUID changedByUserId
    ) {
        return new LeadAuditLog(leadId, previousStatus, newStatus, actionDescription, changedByUserId);
    }

    public UUID getId() {
        return id;
    }

    public UUID getLeadId() {
        return leadId;
    }

    public LeadStatus getPreviousStatus() {
        return previousStatus;
    }

    public LeadStatus getNewStatus() {
        return newStatus;
    }

    public String getActionDescription() {
        return actionDescription;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public UUID getChangedByUserId() {
        return changedByUserId;
    }
}