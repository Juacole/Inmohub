package com.inmohub.property.service.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inmohub.property.service.messaging.events.UserDeletedEvent;
import com.inmohub.property.service.services.PropertyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Listener de Kafka que reacciona al evento de eliminacion de usuario.
 * Cuando un usuario es eliminado del sistema, este listener borra en cascada
 * todas las propiedades asociadas a ese propietario.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaUserDeletedListener {

    private static final String EVENT_USER_DELETED = "USER_DELETED";
    private final PropertyService propertyService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "user.lifecycle.events", groupId = "${spring.kafka.listener.user-lifecycle.group-id}")
    public void onUserDeleted(String message) {
        log.info("Evento recibido en topic user.lifecycle.events");

        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            String eventType = jsonNode.has("eventType") ? jsonNode.get("eventType").asText() : null;

            if (eventType == null) {
                log.warn("Evento sin campo eventType, descartando: {}", message);
                return;
            }

            if (!EVENT_USER_DELETED.equals(eventType)) {
                log.warn("Evento desconocido: {}, descartando", eventType);
                return;
            }

            UserDeletedEvent event = objectMapper.treeToValue(jsonNode, UserDeletedEvent.class);
            propertyService.deleteByOwnerId(event.userId());
            log.info("Propiedades del usuario {} eliminadas correctamente tras recepción de USER_DELETED", event.userId());

        } catch (Exception e) {
            log.error("Error crítico procesando evento USER_DELETED: {}", message, e);
        }
    }
}
