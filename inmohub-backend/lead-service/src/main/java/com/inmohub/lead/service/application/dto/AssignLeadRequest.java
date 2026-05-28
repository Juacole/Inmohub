package com.inmohub.lead.service.application.dto;

import java.util.UUID;

/**
 * DTO de entrada para la asignacion de un lead a un agente.
 * Contiene el ID del agente destino y notas opcionales de la asignacion.
 */
public record AssignLeadRequest(
        UUID agentId,
        String assignmentNotes
) {}