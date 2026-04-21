package com.inmohub.lead.service.domain.ports;

import com.inmohub.lead.service.domain.model.Lead;

import java.util.UUID;

public interface LeadRepository {
    Lead save(Lead lead);
    Lead findById(UUID id);
}
