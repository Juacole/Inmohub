package com.inmohub.lead.service.application.usecases;

import com.inmohub.lead.service.application.dto.LeadResponse;
import com.inmohub.lead.service.application.usecases.errors.PaginationError;
import com.inmohub.lead.service.application.usecases.errors.PropertyNotFoundError;
import com.inmohub.lead.service.domain.abstractions.Error;
import com.inmohub.lead.service.domain.abstractions.PaginatedResult;
import com.inmohub.lead.service.domain.abstractions.Result;
import com.inmohub.lead.service.domain.model.Lead;
import com.inmohub.lead.service.domain.ports.ILeadRepository;

import java.util.List;
import java.util.UUID;

/**
 * Caso de uso para obtener los leads asociados a una propiedad.
 * Valida el ID de propiedad y los parametros de paginacion.
 */
public class GetLeadsByPropertyIdUseCase {
    private final ILeadRepository leadRepository;

    public GetLeadsByPropertyIdUseCase(ILeadRepository leadRepository) {
        this.leadRepository = leadRepository;
    }

    public Result<PaginatedResult<LeadResponse>, Error> execute(UUID propertyId, int page, int size) {
        if (propertyId == null) return Result.error(new PropertyNotFoundError("El ID de la propiedad no puede ser nulo."));
        if (page < 0) return Result.error(new PaginationError("El número de página no puede ser negativo."));
        if (size <= 0) return Result.error(new PaginationError("El tamaño de página debe ser mayor a cero."));
        if (size > 100) return Result.error(new PaginationError("El tamaño de página no puede exceder 100."));

        PaginatedResult<Lead> leadsPage = leadRepository.findByPropertyId(propertyId, page, size);

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