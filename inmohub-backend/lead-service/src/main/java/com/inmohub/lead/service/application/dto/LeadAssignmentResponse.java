package com.inmohub.lead.service.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de salida con los datos de una asignacion de lead a un agente.
 * Contiene el ID del lead, del agente y la fecha de asignacion.
 */
public record LeadAssignmentResponse(
        UUID leadId,
        UUID agentId,
        LocalDateTime assignedtAt
) {
}
