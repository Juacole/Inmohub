package com.inmohub.lead.service.infrastructure.adapters.in.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inmohub.lead.service.application.usecases.DeleteLeadsByPropertyIdUseCase;
import com.inmohub.lead.service.infrastructure.adapters.in.messaging.events.PropertyDeletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaPropertyDeletedListener {

    private static final String EVENT_PROPERTY_DELETED = "PROPERTY_DELETED";
    private final DeleteLeadsByPropertyIdUseCase deleteLeadsByPropertyIdUseCase;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "property.lifecycle.events", groupId = "${spring.kafka.listener.property-lifecycle.group-id}")
    public void onPropertyDeleted(String message) {
        log.info("Evento recibido en topic property.lifecycle.events");

        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            String eventType = jsonNode.has("eventType") ? jsonNode.get("eventType").asText() : null;

            if (eventType == null) {
                log.warn("Evento sin campo eventType, descartando: {}", message);
                return;
            }

            if (!EVENT_PROPERTY_DELETED.equals(eventType)) {
                log.warn("Evento desconocido: {}, descartando", eventType);
                return;
            }

            PropertyDeletedEvent event = objectMapper.treeToValue(jsonNode, PropertyDeletedEvent.class);
            deleteLeadsByPropertyIdUseCase.execute(event.propertyId());
            log.info("Leads asociados a la propiedad {} eliminados correctamente tras recepción de PROPERTY_DELETED", event.propertyId());

        } catch (Exception e) {
            log.error("Error crítico procesando evento PROPERTY_DELETED: {}", message, e);
        }
    }
}
