package com.inmohub.lead.service.application.usecases;

import com.inmohub.lead.service.application.dto.CreateLeadRequest;
import com.inmohub.lead.service.application.dto.LeadResponse;
import com.inmohub.lead.service.domain.model.Lead;
import com.inmohub.lead.service.domain.ports.LeadEventPublisher;
import com.inmohub.lead.service.domain.ports.LeadRepository;

public class CreateLeadUseCase {
    private final LeadRepository leadRepository;
    private final LeadEventPublisher eventPublisher;

    public CreateLeadUseCase(LeadRepository leadRepository, LeadEventPublisher eventPublisher) {
        this.leadRepository = leadRepository;
        this.eventPublisher = eventPublisher;
    }

    public LeadResponse execute(CreateLeadRequest request) {
        Lead newLead = new Lead(
                request.name(),
                request.email(),
                request.phone(),
                request.message(),
                request.source(),
                request.propertyId()
        );

        Lead savedLead = leadRepository.save(newLead);

        eventPublisher.publishLeadCreatedEvent(savedLead);

        return new LeadResponse(
                savedLead.getId(),
                savedLead.getName(),
                savedLead.getEmail(),
                savedLead.getPhone(),
                savedLead.getStatus(),
                savedLead.getPropertyId()
        );
    }
}