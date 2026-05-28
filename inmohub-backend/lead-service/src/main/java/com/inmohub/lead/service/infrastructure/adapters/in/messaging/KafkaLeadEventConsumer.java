package com.inmohub.lead.service.infrastructure.adapters.in.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inmohub.lead.service.application.dto.CreateLeadRequest;
import com.inmohub.lead.service.application.usecases.CreateLeadUseCase;
import com.inmohub.lead.service.domain.model.enums.LeadSource;
import com.inmohub.lead.service.infrastructure.adapters.in.messaging.events.FsboPropertyIngestedEvent;
import com.inmohub.lead.service.infrastructure.adapters.in.messaging.events.IndividualPropertyCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Consumidor Kafka que escucha el topico {@code lead.events}.
 * Procesa eventos de creacion de propiedades FSBO e individuales para generar leads automaticamente.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaLeadEventConsumer {

    private static final String EVENT_FSBO_BULK = "FSBO_OWNER_BULK_UPLOAD";
    private static final String EVENT_INDIVIDUAL_PROPERTY = "INDIVIDUAL_PROPERTY_CREATED";
    private final CreateLeadUseCase createLeadUseCase;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "lead.events", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeLeadEvent(String message) {
        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            String eventType = jsonNode.has("eventType") ? jsonNode.get("eventType").asText() : null;

            if (eventType == null) {
                log.warn("Evento sin campo eventType, descartando: {}", message);
                return;
            }

            switch (eventType) {
                case EVENT_FSBO_BULK -> processFsboBulkEvent(jsonNode);
                case EVENT_INDIVIDUAL_PROPERTY -> processIndividualPropertyEvent(jsonNode);
                default -> log.warn("Evento desconocido: {}, descartando", eventType);
            }
        } catch (Exception e) {
            log.error("Error crítico procesando evento: {}", message, e);
        }
    }

    private void processFsboBulkEvent(JsonNode jsonNode) throws Exception {
        FsboPropertyIngestedEvent event = objectMapper.treeToValue(jsonNode, FsboPropertyIngestedEvent.class);

        CreateLeadRequest leadRequest = new CreateLeadRequest(
                event.ownerName(),
                event.ownerEmail(),
                event.ownerPhone(),
                "Lead (Propietario) generado automáticamente tras carga masiva FSBO. ID Original: " + event.ownerId(),
                LeadSource.FSBO,
                event.propertyId()
        );

        createLeadUseCase.execute(leadRequest);
        log.info("Lead captado desde FSBO Bulk exitosamente. Email: {}", event.ownerEmail());
    }

    private void processIndividualPropertyEvent(JsonNode jsonNode) throws Exception {
        IndividualPropertyCreatedEvent event = objectMapper.treeToValue(jsonNode, IndividualPropertyCreatedEvent.class);

        CreateLeadRequest leadRequest = new CreateLeadRequest(
                event.ownerName(),
                event.ownerEmail(),
                event.ownerPhone(),
                "Lead captado automáticamente tras creación individual de propiedad. ID origen: " + event.ownerId(),
                LeadSource.FSBO,
                event.propertyId()
        );

        createLeadUseCase.execute(leadRequest);
        log.info("Lead captado desde Individual Property Creation. Email: {}", event.ownerEmail());
    }
}