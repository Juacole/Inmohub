package com.inmohub.lead.service.infrastructure.adapters.out.persitence.repositories;

import com.inmohub.lead.service.domain.model.Lead;
import com.inmohub.lead.service.domain.model.LeadAssignment;
import com.inmohub.lead.service.domain.model.LeadAuditLog;
import com.inmohub.lead.service.domain.ports.ILeadRepository;
import com.inmohub.lead.service.domain.abstractions.PaginatedResult;
import com.inmohub.lead.service.infrastructure.adapters.out.persitence.mappers.LeadAssignmentMapper;
import com.inmohub.lead.service.infrastructure.adapters.out.persitence.mappers.LeadEventMapper;
import com.inmohub.lead.service.infrastructure.adapters.out.persitence.mappers.LeadMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    @Override
    public PaginatedResult<Lead> findAll(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Lead> leadsPage = leadRepository.findAll(pageRequest).map(leadMapper::toDomainEntity);

        return PaginatedResult.of(
                leadsPage.getContent(),
                leadsPage.getNumber(),
                leadsPage.getSize(),
                leadsPage.getTotalElements()
        );
    }

    @Override
    public PaginatedResult<Lead> findByPropertyId(UUID propertyId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Lead> leadsPage = leadRepository.findByPropertyId(propertyId, pageRequest)
                .map(leadMapper::toDomainEntity);

        return PaginatedResult.of(
                leadsPage.getContent(),
                leadsPage.getNumber(),
                leadsPage.getSize(),
                leadsPage.getTotalElements()
        );
    }
}