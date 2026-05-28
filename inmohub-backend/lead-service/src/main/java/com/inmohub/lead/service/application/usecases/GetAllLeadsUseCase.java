package com.inmohub.lead.service.application.usecases;

import com.inmohub.lead.service.application.dto.LeadResponse;
import com.inmohub.lead.service.application.usecases.errors.PaginationError;
import com.inmohub.lead.service.domain.abstractions.Error;
import com.inmohub.lead.service.domain.abstractions.PaginatedResult;
import com.inmohub.lead.service.domain.abstractions.Result;
import com.inmohub.lead.service.domain.model.Lead;
import com.inmohub.lead.service.domain.ports.ILeadRepository;

import java.util.List;

/**
 * Caso de uso para obtener todos los leads de forma paginada.
 * Aplica validaciones de paginacion y retorna una pagina de LeadResponse.
 */
public class GetAllLeadsUseCase {
    private final ILeadRepository leadRepository;

    public GetAllLeadsUseCase(ILeadRepository leadRepository) {
        this.leadRepository = leadRepository;
    }

    public Result<PaginatedResult<LeadResponse>, Error> execute(int page, int size) {
        if (page < 0) return Result.error(new PaginationError("El número de página no puede ser negativo."));
        if (size <= 0) return Result.error(new PaginationError("El tamaño de página debe ser mayor a cero."));
        if (size > 100) return Result.error(new PaginationError("El tamaño de página no puede exceder 100."));

        PaginatedResult<Lead> leadsPage = leadRepository.findAll(page, size);

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