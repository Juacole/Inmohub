package com.inmohub.lead.service.infrastructure.adapters.out.messaging;

import com.fasterxml.jackson.databind.DeserializationFeature;
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
@Slf4j
public class FsboIngestionConsumer {

    private final CreateLeadUseCase createLeadUseCase;
    private final ObjectMapper objectMapper;

    public FsboIngestionConsumer(CreateLeadUseCase createLeadUseCase, ObjectMapper objectMapper) {
        this.createLeadUseCase = createLeadUseCase;
        this.objectMapper = objectMapper.copy().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @KafkaListener(topics = "lead.events", groupId = "lead-service-group")
    public void consumeFsboEvent(String message) {
        try {
            FsboPropertyIngestedEvent event = objectMapper.readValue(message, FsboPropertyIngestedEvent.class);

            if (!"FSBO_OWNER_BULK_UPLOAD".equals(event.eventType())) {
                return;
            }

            CreateLeadRequest leadRequest = new CreateLeadRequest(
                    event.ownerName(),
                    event.ownerEmail(),
                    event.ownerPhone(),
                    "Lead (Propietario) generado automáticamente tras carga masiva FSBO. ID Original: " + event.ownerId(),
                    LeadSource.FSBO,
                    null // en una carga masiva no hay IDS asignados a las propiedades
            );

            createLeadUseCase.execute(leadRequest);

            log.info("Lead captado desde FSBO exitosamente. Email: {}", event.ownerEmail());
        } catch (Exception e) {
            log.error("Error crítico procesando evento FSBO: {}", message, e);
        }
    }
}