package com.inmohub.auth.service.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inmohub.auth.service.messaging.events.UserDeletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaUserEventPublisher {

    private static final String TOPIC_USER_LIFECYCLE = "user.lifecycle.events";
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publishUserDeleted(UserDeletedEvent event) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC_USER_LIFECYCLE, event.userId().toString(), jsonPayload);
            log.info("Evento USER_DELETED publicado para userId={}", event.userId());
        } catch (JsonProcessingException e) {
            log.error("Error serializando UserDeletedEvent para userId={}: {}", event.userId(), e.getMessage());
            throw new RuntimeException("Error al serializar evento de borrado de usuario", e);
        }
    }
}
