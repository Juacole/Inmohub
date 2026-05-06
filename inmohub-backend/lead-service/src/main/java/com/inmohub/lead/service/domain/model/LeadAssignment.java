package com.inmohub.lead.service.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class LeadAssignment {
    private UUID id;
    private final UUID leadId;
    private final UUID agentId;
    private final String notes;
    private final LocalDateTime assignedAt;

    private LeadAssignment(UUID leadId, UUID agentId, String notes) {
        this.id = UUID.randomUUID();
        this.leadId = Objects.requireNonNull(leadId, "El ID del lead es obligatorio");
        this.agentId = Objects.requireNonNull(agentId, "El ID del agente es obligatorio");
        this.notes = (notes == null) ? "" : notes;
        this.assignedAt = LocalDateTime.now();
    }

    private LeadAssignment(UUID leadId, UUID agentId, String notes, LocalDateTime assignedAt) {
        this.id = UUID.randomUUID();
        this.leadId = Objects.requireNonNull(leadId, "El ID del lead es obligatorio");
        this.agentId = Objects.requireNonNull(agentId, "El ID del agente es obligatorio");
        this.notes = (notes == null) ? "" : notes;
        this.assignedAt = Objects.requireNonNull(assignedAt, "La fecha de asignación es obligatoria.");
    }

    public static LeadAssignment create(UUID leadId, UUID agentId, String notes) {
        return new LeadAssignment(leadId, agentId, notes);
    }

    public static LeadAssignment reconstitute(UUID id, UUID leadId, UUID agentId, String notes, LocalDateTime assignedAt) {
        LeadAssignment assignment = new LeadAssignment(leadId, agentId, notes, assignedAt);
        assignment.id = id;
        return assignment;
    }

    public UUID getId() {
        return id;
    }

    public UUID getLeadId() {
        return leadId;
    }

    public UUID getAgentId() {
        return agentId;
    }
    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }
    public String getNotes() {
        return notes;
    }
}