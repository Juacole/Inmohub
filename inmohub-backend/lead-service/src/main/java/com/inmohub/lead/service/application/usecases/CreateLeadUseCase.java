package com.inmohub.lead.service.application.usecases;

import com.inmohub.lead.service.application.dto.CreateLeadRequest;
import com.inmohub.lead.service.application.dto.LeadResponse;
import com.inmohub.lead.service.application.usecases.errors.ValidationError;
import com.inmohub.lead.service.domain.abstractions.Error;
import com.inmohub.lead.service.domain.abstractions.Result;
import com.inmohub.lead.service.domain.model.Lead;
import com.inmohub.lead.service.domain.ports.ILeadRepository;
import com.inmohub.lead.service.domain.valueobjetcs.Email;

public class CreateLeadUseCase {
    private final ILeadRepository leadRepository;

    public CreateLeadUseCase(ILeadRepository leadRepository) {
        this.leadRepository = leadRepository;
    }

    public Result<LeadResponse, Error> execute(CreateLeadRequest request) {
        if (request == null) return Result.error(new ValidationError("La solicitud no puede ser nula."));
        if (request.name() == null || request.name().isBlank()) return Result.error(new ValidationError("El nombre es obligatorio."));
        if (request.email() == null || request.email().isBlank()) return Result.error(new ValidationError("El email es obligatorio."));

        Email email;
        try {
            email = new Email(request.email());
        } catch (IllegalArgumentException e) {
            return Result.error(new ValidationError("El formato del email es inválido."));
        }

        if (request.phone() == null || request.phone().isBlank()) return Result.error(new ValidationError("El teléfono es obligatorio."));

        if (request.propertyId() == null) return Result.error(new ValidationError("La propiedad es obligatoria."));


        Lead newLead = Lead.create(
                request.name(),
                email,
                request.phone(),
                request.message(),
                request.source(),
                request.propertyId()
        );

        Lead savedLead = leadRepository.saveLead(newLead);

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