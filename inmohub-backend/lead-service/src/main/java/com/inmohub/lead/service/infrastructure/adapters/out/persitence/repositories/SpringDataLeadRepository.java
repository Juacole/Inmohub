package com.inmohub.lead.service.infrastructure.adapters.out.persitence.repositories;

import com.inmohub.lead.service.infrastructure.adapters.out.persitence.entities.LeadJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repositorio Spring Data JPA para la entidad {@link LeadJpaEntity}.
 * Ofrece busqueda paginada por ID de propiedad y eliminacion por ID de propiedad.
 */
public interface SpringDataLeadRepository extends JpaRepository<LeadJpaEntity, UUID> {
    Page<LeadJpaEntity> findByPropertyId(UUID propertyId, Pageable pageable);
    void deleteByPropertyId(UUID propertyId);
}
