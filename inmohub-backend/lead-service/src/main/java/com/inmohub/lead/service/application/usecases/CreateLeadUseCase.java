package com.inmohub.lead.service.application.usecases;

import com.inmohub.lead.service.application.dto.CreateLeadRequest;
import com.inmohub.lead.service.application.dto.LeadResponse;
import com.inmohub.lead.service.domain.abstractions.Error;
import com.inmohub.lead.service.domain.abstractions.Result;
import com.inmohub.lead.service.domain.model.Lead;
import com.inmohub.lead.service.domain.ports.ILeadEventPublisher;
import com.inmohub.lead.service.domain.ports.ILeadRepository;
import com.inmohub.lead.service.domain.valueobjetcs.Email;

public class CreateLeadUseCase {
    private final ILeadRepository leadRepository;
    private final ILeadEventPublisher eventPublisher;

    public CreateLeadUseCase(ILeadRepository leadRepository, ILeadEventPublisher eventPublisher) {
        this.leadRepository = leadRepository;
        this.eventPublisher = eventPublisher;
    }

    public Result<LeadResponse, Error> execute(CreateLeadRequest request) {
        Lead newLead = Lead.create(
                request.name(),
                new Email(request.email()),
                request.phone(),
                request.message(),
                request.source(),
                request.propertyId()
        );

        Lead savedLead = leadRepository.saveLead(newLead);

        eventPublisher.publishLeadCreatedEvent(savedLead);

        return Result.success(
                new LeadResponse(
                        savedLead.getId(),
                        savedLead.getName(),
                        savedLead.getEmail().value(),
                        savedLead.getPhone(),
                        savedLead.getStatus(),
                        savedLead.getPropertyId()
                )
        );
    }
}