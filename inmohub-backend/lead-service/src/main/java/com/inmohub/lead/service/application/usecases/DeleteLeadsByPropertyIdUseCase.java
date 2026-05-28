package com.inmohub.lead.service.application.usecases;

import com.inmohub.lead.service.application.usecases.errors.ValidationError;
import com.inmohub.lead.service.domain.abstractions.Error;
import com.inmohub.lead.service.domain.abstractions.Result;
import com.inmohub.lead.service.domain.abstractions.Unit;
import com.inmohub.lead.service.domain.ports.ILeadRepository;

import java.util.UUID;

/**
 * Caso de uso para eliminar todos los leads asociados a una propiedad.
 * Se invoca cuando una propiedad es eliminada del sistema.
 */
public class DeleteLeadsByPropertyIdUseCase {
    private final ILeadRepository leadRepository;

    public DeleteLeadsByPropertyIdUseCase(ILeadRepository leadRepository) {
        this.leadRepository = leadRepository;
    }

    public Result<Unit, Error> execute(UUID propertyId) {
        if (propertyId == null) {
            return Result.error(new ValidationError("El ID de la propiedad no puede ser nulo."));
        }
        leadRepository.deleteByPropertyId(propertyId);
        return Result.success(Unit.INSTANCE);
    }
}
