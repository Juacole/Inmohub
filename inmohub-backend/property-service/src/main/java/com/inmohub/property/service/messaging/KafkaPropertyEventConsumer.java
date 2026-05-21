package com.inmohub.property.service.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.inmohub.property.service.messaging.events.BulkPropertyEvent;
import com.inmohub.property.service.services.PropertyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaPropertyEventConsumer {

    private final PropertyService propertyService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "property.bulk.create", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeBulkProperties(String message) {
        log.info("Evento recibido en topic property.bulk.create");

        try {
            BulkPropertyEvent bulkEvent = objectMapper.readValue(message, BulkPropertyEvent.class);
            propertyService.processBulkProperties(bulkEvent);
        } catch (JsonProcessingException e) {
            log.error("Error crítico procesando el lote de propiedades desde Kafka: {}", e.getMessage(), e);
        }
    }
}