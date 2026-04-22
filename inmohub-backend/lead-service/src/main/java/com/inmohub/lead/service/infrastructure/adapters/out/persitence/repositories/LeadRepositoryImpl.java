package com.inmohub.lead.service.infrastructure.adapters.out.persitence.repositories;

import com.inmohub.lead.service.domain.model.Lead;
import com.inmohub.lead.service.domain.model.LeadAssignment;
import com.inmohub.lead.service.domain.model.LeadAuditLog;
import com.inmohub.lead.service.domain.ports.ILeadRepository;
import com.inmohub.lead.service.infrastructure.adapters.out.persitence.mappers.LeadAssignmentMapper;
import com.inmohub.lead.service.infrastructure.adapters.out.persitence.mappers.LeadEventMapper;
import com.inmohub.lead.service.infrastructure.adapters.out.persitence.mappers.LeadMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class LeadRepositoryImpl implements ILeadRepository {

    private final SpringDataLeadRepository leadRepository;
    private final SpringDataLeadAssignmentRepository assignmentRepository;
    private final SpringDataLeadEventRepository eventRepository;
    private final LeadMapper leadMapper;
    private final LeadEventMapper eventMapper;
    private final LeadAssignmentMapper assignmentMapper;

    @Override
    public Lead saveLead(Lead lead) {
        return leadMapper.toDomainEntity(
                leadRepository.save(
                        leadMapper.toJpaEntity(lead)
                )
        );
    }

    @Override
    public Lead findById(UUID id) {
        return leadRepository.findById(id).map(leadMapper::toDomainEntity).orElse(null);
    }

    @Override
    public void saveAssignment(LeadAssignment assignment) {
        assignmentRepository.save(assignmentMapper.toJpaEntity(assignment));
    }

    @Override
    public void saveAuditLog(LeadAuditLog auditLog) {
        eventRepository.save(eventMapper.toJpaEntity(auditLog));
    }
}