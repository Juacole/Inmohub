package com.inmohub.lead.service.infrastructure.adapters.out.persitence.repositories;

import com.inmohub.lead.service.infrastructure.adapters.out.persitence.entities.LeadAssignmentJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio Spring Data JPA para la entidad {@link LeadAssignmentJpaEntity}.
 * Ofrece busqueda paginada por ID de agente y busqueda por ID de lead.
 */
public interface SpringDataLeadAssignmentRepository extends JpaRepository<LeadAssignmentJpaEntity, UUID> {
    Page<LeadAssignmentJpaEntity> findByAgentId(UUID agentId, Pageable pageable);
    List<LeadAssignmentJpaEntity> findByLeadId(UUID leadId);
}
