package com.inmohub.lead.service.application.dto;

import com.inmohub.lead.service.domain.model.enums.LeadSource;

import java.util.UUID;

public record CreateLeadRequest(
        String name,
        String email,
        String phone,
        String message,
        LeadSource source,
        UUID propertyId
) {
}
