package com.inmohub.lead.service.infrastructure.adapters.out.persitence.mappers;

import com.inmohub.lead.service.domain.model.Lead;
import com.inmohub.lead.service.infrastructure.adapters.out.persitence.entities.LeadJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LeadMapper {
    LeadJpaEntity toJpaEntity(Lead domainLead);
    Lead toDomainEntity(LeadJpaEntity jpaEntity);
}