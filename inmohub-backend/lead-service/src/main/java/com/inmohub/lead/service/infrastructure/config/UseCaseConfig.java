package com.inmohub.lead.service.infrastructure.config;

import com.inmohub.lead.service.application.usecases.AssignLeadUseCase;
import com.inmohub.lead.service.application.usecases.CreateLeadUseCase;
import com.inmohub.lead.service.application.usecases.GetAllLeadsUseCase;
import com.inmohub.lead.service.application.usecases.GetLeadsByAgentIdUseCase;
import com.inmohub.lead.service.application.usecases.GetLeadsByPropertyIdUseCase;
import com.inmohub.lead.service.domain.ports.ILeadRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class UseCaseConfig {

    @Bean
    @Transactional
    public CreateLeadUseCase createLeadUseCase(
            ILeadRepository leadRepository
    ) {
        return new CreateLeadUseCase(leadRepository);
    }

    @Bean
    @Transactional
    public AssignLeadUseCase assignLeadUseCase(ILeadRepository leadRepository) {
        return new AssignLeadUseCase(leadRepository);
    }

    @Bean
    @Transactional
    public GetAllLeadsUseCase getAllLeadsUseCase(ILeadRepository leadRepository) {
        return new GetAllLeadsUseCase(leadRepository);
    }

    @Bean
    @Transactional
    public GetLeadsByPropertyIdUseCase getLeadsByPropertyIdUseCase(ILeadRepository leadRepository) {
        return new GetLeadsByPropertyIdUseCase(leadRepository);
    }

    @Bean
    @Transactional
    public GetLeadsByAgentIdUseCase getLeadsByAgentIdUseCase(ILeadRepository leadRepository) {
        return new GetLeadsByAgentIdUseCase(leadRepository);
    }
}
