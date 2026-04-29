package com.inmohub.property.service.messaging;

import com.inmohub.property.service.messaging.dtos.BulkPropertyEventDto;
import com.inmohub.property.service.services.PropertyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PropertyEventConsumer {

    private final PropertyService propertyService;

    @KafkaListener(topics = "property.bulk.create", groupId = "property-group")
    public void consumeBulkProperties(BulkPropertyEventDto bulkEvent) {
        log.info("Evento recibido en topic property.bulk.create para owner: {}", bulkEvent.ownerId());

        try {
            propertyService.processBulkProperties(bulkEvent);
        } catch (Exception e) {
            log.error("Error crítico procesando el lote de propiedades desde Kafka: {}", e.getMessage(), e);
            throw e;
        }
    }
}