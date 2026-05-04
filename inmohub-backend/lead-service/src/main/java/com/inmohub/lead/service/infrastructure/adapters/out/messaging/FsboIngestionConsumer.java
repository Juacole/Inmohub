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

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class FsboIngestionConsumer {

    private final CreateLeadUseCase createLeadUseCase;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "lead.events", groupId = "${spring.kafka.consumer.group-id}")
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
                    event.propertyId()
            );

            createLeadUseCase.execute(leadRequest);

            log.info("Lead captado desde FSBO exitosamente. Email: {}", event.ownerEmail());
        } catch (Exception e) {
            log.error("Error crítico procesando evento FSBO: {}", message, e);
        }
    }
}