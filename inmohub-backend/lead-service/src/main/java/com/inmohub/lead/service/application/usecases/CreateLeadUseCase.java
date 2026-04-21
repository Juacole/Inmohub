package com.inmohub.lead.service.application.usecases;

import com.inmohub.lead.service.application.dto.CreateLeadRequest;
import com.inmohub.lead.service.application.dto.LeadResponse;
import com.inmohub.lead.service.application.usecases.errors.InvalidEmailFormat;
import com.inmohub.lead.service.domain.abstractions.Error;
import com.inmohub.lead.service.domain.abstractions.Result;
import com.inmohub.lead.service.domain.model.Lead;
import com.inmohub.lead.service.domain.ports.LeadEventPublisher;
import com.inmohub.lead.service.domain.ports.LeadRepository;
import com.inmohub.lead.service.domain.valueobjetcs.Email;

public class CreateLeadUseCase {
    private final LeadRepository leadRepository;
    private final LeadEventPublisher eventPublisher;

    public CreateLeadUseCase(LeadRepository leadRepository, LeadEventPublisher eventPublisher) {
        this.leadRepository = leadRepository;
        this.eventPublisher = eventPublisher;
    }

    public Result<LeadResponse, Error> execute(CreateLeadRequest request) {
        if(Email.isValidEmail(request.email()))
            return Result.error(new InvalidEmailFormat("El formato del email es incorrecto.", null));

        Lead newLead = Lead.create(
                request.name(),
                new Email(request.email()),
                request.phone(),
                request.message(),
                request.source(),
                request.propertyId()
        );

        Lead savedLead = leadRepository.save(newLead);

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