package com.inmohub.lead.service.infrastructure.adapters.out.persitence.repositories;

import com.inmohub.lead.service.domain.model.Lead;
import com.inmohub.lead.service.domain.ports.ILeadRepository;
import com.inmohub.lead.service.infrastructure.adapters.out.persitence.mappers.LeadMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class LeadRepositoryImpl implements ILeadRepository {

    private final SpringDataLeadRepository jpaRepository;
    private final LeadMapper leadMapper;

    @Override
    public Lead save(Lead lead) {
        return leadMapper.toDomainEntity(
                jpaRepository.save(
                        leadMapper.toJpaEntity(lead)
                )
        );
    }

    @Override
    public Lead findById(UUID id) {
        return leadMapper.toDomainEntity(
                jpaRepository.findById(id).get()
        );
    }
}