package com.inmohub.lead.service.infrastructure.adapters.out.persitence.mappers;

import com.inmohub.lead.service.domain.model.Lead;
import com.inmohub.lead.service.domain.model.enums.LeadSource;
import com.inmohub.lead.service.domain.model.enums.LeadStatus;
import com.inmohub.lead.service.domain.valueobjetcs.Email;
import com.inmohub.lead.service.infrastructure.adapters.out.persitence.entities.LeadJpaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ObjectFactory;

import java.util.UUID;

/**
 * Mapper MapStruct/DTO manual que convierte entre la entidad de dominio {@link Lead}
 * y la entidad JPA {@link LeadJpaEntity} para persistencia.
 */
@Mapper(componentModel = "spring")
public interface LeadMapper {

    default LeadJpaEntity toJpaEntity(Lead domain) {
        if (domain == null) return null;

        LeadJpaEntity entity = new LeadJpaEntity();
        entity.setId((UUID) domain.getId());
        entity.setEmail(domain.getEmail() != null ? domain.getEmail().value() : null);
        entity.setName(domain.getName());
        entity.setPhone(domain.getPhone());
        entity.setMessage(domain.getMessage());
        entity.setSource(domain.getSource());
        entity.setPropertyId(domain.getPropertyId());
        entity.setStatus(domain.getStatus());

        return entity;
    }

    default Lead toDomainEntity(LeadJpaEntity entity) {
        if (entity == null) return null;

        return Lead.reconstitute(
                entity.getId(),
                entity.getName(),
                new Email(entity.getEmail()),
                entity.getPhone(),
                entity.getMessage(),
                entity.getSource(),
                entity.getPropertyId(),
                entity.getStatus()
        );
    }
}