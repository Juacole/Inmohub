package com.inmohub.lead.service.application.usecases;

import com.inmohub.lead.service.application.dto.LeadResponse;
import com.inmohub.lead.service.application.usecases.errors.ForbiddenError;
import com.inmohub.lead.service.application.usecases.errors.LeadNotFound;
import com.inmohub.lead.service.application.usecases.errors.InvalidStatusError;
import com.inmohub.lead.service.application.usecases.errors.ValidationError;
import com.inmohub.lead.service.domain.abstractions.Error;
import com.inmohub.lead.service.domain.abstractions.Result;
import com.inmohub.lead.service.domain.model.Lead;
import com.inmohub.lead.service.domain.model.LeadAssignment;
import com.inmohub.lead.service.domain.model.enums.LeadStatus;
import com.inmohub.lead.service.domain.ports.ILeadRepository;

import java.util.List;
import java.util.UUID;

/**
 * Caso de uso para cambiar el estado de un lead.
 * Verifica que el solicitante tenga permisos (agente asignado o admin)
 * y aplica la transicion de estado solicitada.
 */
public class ChangeLeadStatusUseCase {
    private final ILeadRepository leadRepository;

    public ChangeLeadStatusUseCase(ILeadRepository leadRepository) {
        this.leadRepository = leadRepository;
    }

    public Result<LeadResponse, Error> execute(UUID leadId, String newStatus, UUID requesterId, String requesterRole) {
        if (leadId == null) return Result.error(new ValidationError("El ID del lead no puede ser nulo."));
        if (newStatus == null || newStatus.isBlank()) return Result.error(new ValidationError("El nuevo estado no puede estar vacío."));
        if (requesterId == null) return Result.error(new ValidationError("El ID del solicitante no puede ser nulo."));
        if (requesterRole == null || requesterRole.isBlank()) return Result.error(new ValidationError("El rol del solicitante no puede estar vacío."));

        Lead lead = leadRepository.findById(leadId);
        if (lead == null) {
            return Result.error(new LeadNotFound("Lead no encontrado."));
        }

        if ("ROLE_ADMIN".equals(requesterRole)) {
            // Admin puede modificar cualquier lead
        } else if ("ROLE_AGENT".equals(requesterRole)) {
            List<LeadAssignment> assignments = leadRepository.findAssignmentsByLeadId(leadId);
            boolean isAssigned = assignments.stream()
                    .anyMatch(a -> a.getAgentId().equals(requesterId));

            if (!isAssigned) {
                return Result.error(new ForbiddenError("No tienes permisos para modificar este lead."));
            }
        } else {
            return Result.error(new ForbiddenError("No tienes permisos para modificar este lead."));
        }

        LeadStatus status;
        try {
            status = LeadStatus.valueOf(newStatus.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Result.error(new InvalidStatusError("Estado de lead inválido."));
        }

        lead.updateStatus(status);
        Lead savedLead = leadRepository.saveLead(lead);

        LeadResponse response = new LeadResponse(
                savedLead.getId(),
                savedLead.getName(),
                savedLead.getEmail().value(),
                savedLead.getPhone(),
                savedLead.getStatus(),
                savedLead.getPropertyId()
        );

        return Result.success(response);
    }
}