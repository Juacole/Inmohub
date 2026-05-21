package com.inmohub.property.service.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inmohub.property.service.dtos.UserResponseDto;
import com.inmohub.property.service.messaging.events.IndividualPropertyCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaLeadEventPublisher {

    private static final String TOPIC = "lead.events";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publishIndividualPropertyLead(UUID propertyId, UserResponseDto ownerDetails) {
        IndividualPropertyCreatedEvent event = new IndividualPropertyCreatedEvent(
                "INDIVIDUAL_PROPERTY_CREATED",
                propertyId,
                ownerDetails.id(),
                ownerDetails.name(),
                ownerDetails.email(),
                ownerDetails.phone(),
                "INDIVIDUAL_UPLOAD"
        );

        try {
            String jsonMessage = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC, ownerDetails.id().toString(), jsonMessage);
            log.info("Evento IndividualPropertyCreatedEvent publicado al topic {} para propertyId {}", TOPIC, propertyId);
        } catch (JsonProcessingException e) {
            log.error("Error al serializar el evento IndividualPropertyCreatedEvent: {}", e.getMessage(), e);
        }
    }
}