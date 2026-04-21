package com.inmohub.lead.service.infrastructure.config;

import com.inmohub.lead.service.application.usecases.CreateLeadUseCase;
import com.inmohub.lead.service.domain.ports.ILeadEventPublisher;
import com.inmohub.lead.service.domain.ports.ILeadRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public CreateLeadUseCase createLeadUseCase(
            ILeadRepository leadRepository,
            ILeadEventPublisher leadEventPublisher
    ) {
        return new CreateLeadUseCase(leadRepository, leadEventPublisher);
    }
}
