package com.inmohub.lead.service.application.usecases;

import com.inmohub.lead.service.application.dto.LeadResponse;
import com.inmohub.lead.service.application.usecases.errors.AgentNotFoundError;
import com.inmohub.lead.service.application.usecases.errors.PaginationError;
import com.inmohub.lead.service.domain.abstractions.Error;
import com.inmohub.lead.service.domain.abstractions.PaginatedResult;
import com.inmohub.lead.service.domain.abstractions.Result;
import com.inmohub.lead.service.domain.model.Lead;
import com.inmohub.lead.service.domain.ports.ILeadRepository;

import java.util.List;
import java.util.UUID;

public class GetLeadsByAgentIdUseCase {
    private final ILeadRepository leadRepository;

    public GetLeadsByAgentIdUseCase(ILeadRepository leadRepository) {
        this.leadRepository = leadRepository;
    }

    public Result<PaginatedResult<LeadResponse>, Error> execute(UUID agentId, int page, int size) {
        if (agentId == null) return Result.error(new AgentNotFoundError("El ID del agente no puede ser nulo."));
        if (page < 0) return Result.error(new PaginationError("El número de página no puede ser negativo."));
        if (size <= 0) return Result.error(new PaginationError("El tamaño de página debe ser mayor a cero."));
        if (size > 100) return Result.error(new PaginationError("El tamaño de página no puede exceder 100."));

        PaginatedResult<Lead> leadsPage = leadRepository.findByAgentId(agentId, page, size);

        List<LeadResponse> content = leadsPage.content().stream()
                .map(lead -> new LeadResponse(
                        lead.getId(),
                        lead.getName(),
                        lead.getEmail().value(),
                        lead.getPhone(),
                        lead.getStatus(),
                        lead.getPropertyId()
                ))
                .toList();

        PaginatedResult<LeadResponse> response = PaginatedResult.of(
                content,
                leadsPage.page(),
                leadsPage.size(),
                leadsPage.totalElements()
        );

        return Result.success(response);
    }
}