package com.inmohub.lead.service.infrastructure.config;

import com.inmohub.lead.service.application.usecases.AssignLeadUseCase;
import com.inmohub.lead.service.application.usecases.ChangeLeadStatusUseCase;
import com.inmohub.lead.service.application.usecases.CreateLeadUseCase;
import com.inmohub.lead.service.application.usecases.DeleteLeadsByPropertyIdUseCase;
import com.inmohub.lead.service.application.usecases.GetAllLeadsUseCase;
import com.inmohub.lead.service.application.usecases.GetLeadsByAgentIdUseCase;
import com.inmohub.lead.service.application.usecases.GetLeadsByPropertyIdUseCase;
import com.inmohub.lead.service.domain.ports.ILeadRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

/**
 * Configuracion de Spring que expone los casos de uso como beans gestionados por el contenedor.
 * Cada caso de uso recibe el repositorio de leads como dependencia.
 */
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

    @Bean
    @Transactional
    public ChangeLeadStatusUseCase changeLeadStatusUseCase(ILeadRepository leadRepository) {
        return new ChangeLeadStatusUseCase(leadRepository);
    }

    @Bean
    @Transactional
    public DeleteLeadsByPropertyIdUseCase deleteLeadsByPropertyIdUseCase(ILeadRepository leadRepository) {
        return new DeleteLeadsByPropertyIdUseCase(leadRepository);
    }
}
