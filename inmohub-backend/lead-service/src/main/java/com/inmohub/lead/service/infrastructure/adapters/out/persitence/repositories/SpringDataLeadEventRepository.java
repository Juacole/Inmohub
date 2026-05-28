package com.inmohub.lead.service.infrastructure.adapters.out.persitence.repositories;

import com.inmohub.lead.service.infrastructure.adapters.out.persitence.entities.LeadEventJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repositorio Spring Data JPA para la entidad {@link LeadEventJpaEntity}.
 * Proporciona operaciones CRUD basicas para los eventos de auditoria de leads.
 */
public interface SpringDataLeadEventRepository extends JpaRepository<LeadEventJpaEntity, UUID> {
}
