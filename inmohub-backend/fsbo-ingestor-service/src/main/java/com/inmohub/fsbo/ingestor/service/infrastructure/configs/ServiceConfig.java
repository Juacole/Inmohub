package com.inmohub.fsbo.ingestor.service.infrastructure.configs;

import com.inmohub.fsbo.ingestor.service.application.services.DeduplicationService;
import com.inmohub.fsbo.ingestor.service.domain.ports.IFsboRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig {

    @Bean
    public DeduplicationService deduplicationService(IFsboRepository fsboRepository) {
        return new DeduplicationService(fsboRepository);
    }
}
