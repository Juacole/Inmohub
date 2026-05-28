package com.inmohub.fsbo.ingestor.service.infrastructure.adapters.out.persistence.repositories;

import com.inmohub.fsbo.ingestor.service.domain.models.FsboBatch;
import com.inmohub.fsbo.ingestor.service.domain.models.enums.RecordStatus;
import com.inmohub.fsbo.ingestor.service.domain.ports.IFsboRepository;
import com.inmohub.fsbo.ingestor.service.infrastructure.adapters.out.persistence.entities.FsboSubmissionJpaEntity;
import com.inmohub.fsbo.ingestor.service.infrastructure.adapters.out.persistence.mappers.FsboMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementacion del puerto {@link IFsboRepository} usando JPA.
 * Delega en {@link FsboSubmissionJpaRepository} y {@link FsboMapper} para persistir lotes.
 */
@Repository
@RequiredArgsConstructor
public class FsboRepositoryImpl implements IFsboRepository {

    private final FsboSubmissionJpaRepository jpaRepository;
    private final FsboMapper fsboMapper;

    @Override
    @Transactional
    public void saveBatch(FsboBatch batch) {
        List<FsboSubmissionJpaEntity> entities = batch.getProperties().stream()
                .map(record -> fsboMapper.toEntity(record, batch.getOwnerDetails()))
                .toList();

        jpaRepository.saveAll(entities);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByAddressAndCity(String address, String city) {
        return jpaRepository.existsByAddressIgnoreCaseAndCityIgnoreCaseAndStatus(address, city, RecordStatus.PROCESSED.name());
    }
}