package com.inmohub.lead.service.infrastructure.adapters.out.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inmohub.lead.service.application.dto.CreateLeadRequest;
import com.inmohub.lead.service.application.usecases.CreateLeadUseCase;
import com.inmohub.lead.service.domain.model.enums.LeadSource;
import com.inmohub.lead.service.infrastructure.adapters.out.messaging.events.FsboPropertyIngestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FsboIngestionConsumer {

    private final CreateLeadUseCase createLeadUseCase;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "fsbo.events", groupId = "lead-service-group")
    public void consumeFsboEvent(String message) {
        try {
            // Mapeo del mensaje enviado por fsbo-service
            FsboPropertyIngestedEvent event = objectMapper.readValue(message, FsboPropertyIngestedEvent.class);

            CreateLeadRequest leadRequest = new CreateLeadRequest(
                    event.ownerName(),
                    event.ownerEmail(),
                    event.ownerPhone(),
                    "Lead generado automáticamente tras ingesta de propiedad FSBO: " + event.ingestionSource(),
                    LeadSource.FSBO,
                    event.propertyId()
            );

            createLeadUseCase.execute(leadRequest);

            log.info("Lead captado desde FSBO.");
        } catch (Exception e) {
            log.error("Error procesando evento FSBO: ", e);
        }
    }
}