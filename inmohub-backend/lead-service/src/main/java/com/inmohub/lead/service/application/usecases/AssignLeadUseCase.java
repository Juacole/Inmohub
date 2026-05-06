package com.inmohub.lead.service.application.usecases;

import com.inmohub.lead.service.application.dto.AssignLeadRequest;
import com.inmohub.lead.service.application.dto.LeadAssignmentResponse;
import com.inmohub.lead.service.application.usecases.errors.LeadNotFound;
import com.inmohub.lead.service.domain.abstractions.Error;
import com.inmohub.lead.service.domain.abstractions.Result;
import com.inmohub.lead.service.domain.model.Lead;
import com.inmohub.lead.service.domain.model.LeadAssignment;
import com.inmohub.lead.service.domain.model.LeadAuditLog;
import com.inmohub.lead.service.domain.ports.ILeadRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public class AssignLeadUseCase {
    private final ILeadRepository leadRepository;

    public AssignLeadUseCase(ILeadRepository leadRepository) {
        this.leadRepository = leadRepository;
    }

    public Result<LeadAssignmentResponse, Error> execute(UUID leadId, AssignLeadRequest request, UUID actionUserId) {
        Lead lead = leadRepository.findById(leadId);
        if (lead == null)
            return Result.error(new LeadNotFound("Lead no encontrado o null."));

        var previousStatus = lead.getStatus();

        lead.contactLead();
        leadRepository.saveLead(lead);

        LeadAssignment assignment = LeadAssignment.create(lead.getId(), request.agentId(), request.assignmentNotes());
        leadRepository.saveAssignment(assignment);

        LeadAuditLog auditLog = LeadAuditLog.create(
                lead.getId(),
                previousStatus,
                lead.getStatus(),
                "Lead asignado al agente: " + request.agentId(),
                actionUserId
        );
        leadRepository.saveAuditLog(auditLog);

        return Result.success(
                new LeadAssignmentResponse(lead.getId(), request.agentId(), LocalDateTime.now())
        );
    }
}