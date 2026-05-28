package com.inmohub.lead.service.application.dto;

import com.inmohub.lead.service.domain.model.enums.LeadSource;

import java.util.UUID;

/**
 * DTO de entrada para la creacion de un nuevo lead.
 * Transporta los datos del contacto, el origen y la propiedad asociada.
 */
public record CreateLeadRequest(
        String name,
        String email,
        String phone,
        String message,
        LeadSource source,
        UUID propertyId
) {
}
