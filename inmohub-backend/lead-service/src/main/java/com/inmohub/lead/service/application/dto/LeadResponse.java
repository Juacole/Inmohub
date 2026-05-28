package com.inmohub.lead.service.application.dto;

import com.inmohub.lead.service.domain.model.enums.LeadStatus;

import java.util.UUID;

/**
 * DTO de salida con los datos basicos de un lead para las respuestas de la API.
 * Incluye el identificador, nombre, email, telefono, estado y propiedad asociada.
 */
public record LeadResponse(
        UUID id,
        String name,
        String email,
        String phone,
        LeadStatus status,
        UUID propertyId
) {
}
