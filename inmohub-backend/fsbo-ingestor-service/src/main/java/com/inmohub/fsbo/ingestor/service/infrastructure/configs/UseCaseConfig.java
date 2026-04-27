package com.inmohub.fsbo.ingestor.service.infrastructure.configs;

import com.inmohub.fsbo.ingestor.service.application.usecases.IngestFsboFileUseCase;
import com.inmohub.fsbo.ingestor.service.domain.ports.ICsvParser;
import com.inmohub.fsbo.ingestor.service.domain.ports.IFsboRepository;
import com.inmohub.fsbo.ingestor.service.domain.ports.ILeadEventPublisher;
import com.inmohub.fsbo.ingestor.service.domain.services.DeduplicationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class UseCaseConfig {

    @Bean
    @Transactional
    public IngestFsboFileUseCase ingestFsboFileUseCase(
            ICsvParser csvParser,
            DeduplicationService deduplicationService,
            IFsboRepository fsboRepository,
            ILeadEventPublisher leadEventPublisher
    ) {
        return new IngestFsboFileUseCase(csvParser, deduplicationService, fsboRepository, leadEventPublisher);
    }
}
