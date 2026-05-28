package com.inmohub.property.service.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inmohub.property.service.messaging.events.PropertyDeletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Publicador de eventos del ciclo de vida de propiedades hacia Kafka.
 * Emite eventos PropertyDeletedEvent al topic property.lifecycle.events
 * cuando se elimina una propiedad, notificando a otros servicios del ecosistema.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaPropertyEventPublisher {

    private static final String TOPIC = "property.lifecycle.events";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publishPropertyDeleted(PropertyDeletedEvent event) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC, event.propertyId().toString(), jsonMessage);
            log.info("Evento PropertyDeletedEvent publicado al topic {} para propertyId {}", TOPIC, event.propertyId());
        } catch (JsonProcessingException e) {
            log.error("Error al serializar el evento PropertyDeletedEvent: {}", e.getMessage(), e);
        }
    }
}
