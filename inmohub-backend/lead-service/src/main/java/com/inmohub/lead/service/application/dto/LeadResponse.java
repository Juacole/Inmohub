package com.inmohub.lead.service.application.dto;

import com.inmohub.lead.service.domain.model.enums.LeadStatus;

import java.util.UUID;

public record LeadResponse(
        UUID id,
        String name,
        String email,
        String phone,
        LeadStatus status,
        UUID propertyId
) {
}
