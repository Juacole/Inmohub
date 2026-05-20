package com.inmohub.lead.service.domain.ports;

import com.inmohub.lead.service.domain.model.Lead;
import com.inmohub.lead.service.domain.model.LeadAssignment;
import com.inmohub.lead.service.domain.model.LeadAuditLog;
import com.inmohub.lead.service.domain.abstractions.PaginatedResult;

import java.util.List;
import java.util.UUID;

public interface ILeadRepository {
    Lead saveLead(Lead lead);
    Lead findById(UUID id);
    List<LeadAssignment> findAssignmentsByLeadId(UUID leadId);
    void saveAssignment(LeadAssignment assignment);
    void saveAuditLog(LeadAuditLog auditLog);
    PaginatedResult<Lead> findAll(int page, int size);
    PaginatedResult<Lead> findByPropertyId(UUID propertyId, int page, int size);
    PaginatedResult<Lead> findByAgentId(UUID agentId, int page, int size);
    void deleteByPropertyId(UUID propertyId);
}
