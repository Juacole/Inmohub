package com.inmohub.lead.service.application.dto;

import com.inmohub.lead.service.domain.abstractions.DomainException;

/**
 * DTO de entrada para cambiar el estado de un lead.
 * Contiene el nuevo estado que se desea asignar.
 */
public record ChangeStatusRequest(String status) {
    public ChangeStatusRequest {
        if (status.isBlank()) throw new DomainException("El status no puede estar vacío ni ser nulo.");
        status.toUpperCase().trim();
    }
}