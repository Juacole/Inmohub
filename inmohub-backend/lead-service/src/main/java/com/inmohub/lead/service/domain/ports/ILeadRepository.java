package com.inmohub.lead.service.domain.ports;

import com.inmohub.lead.service.domain.model.Lead;

import java.util.Optional;
import java.util.UUID;

public interface ILeadRepository {
    Lead save(Lead lead);
    Lead findById(UUID id);
}
