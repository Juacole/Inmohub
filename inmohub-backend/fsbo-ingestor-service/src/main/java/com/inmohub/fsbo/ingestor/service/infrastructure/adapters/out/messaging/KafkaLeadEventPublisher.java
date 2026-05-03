package com.inmohub.fsbo.ingestor.service.infrastructure.adapters.out.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inmohub.fsbo.ingestor.service.domain.models.FsboBatch;
import com.inmohub.fsbo.ingestor.service.domain.models.OwnerDetails;
import com.inmohub.fsbo.ingestor.service.domain.models.PropertyRecord;
import com.inmohub.fsbo.ingestor.service.domain.ports.ILeadEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaLeadEventPublisher implements ILeadEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private static final String TOPIC_LEADS = "lead.events";

    @Override
    public void publishOwnerAsLeadEvent(FsboBatch batch) {
        OwnerDetails owner = batch.getOwnerDetails();

        for (PropertyRecord record : batch.getValidProperties()) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("eventType", "FSBO_OWNER_BULK_UPLOAD");
            payload.put("ownerId", owner.ownerId().toString());
            payload.put("ownerName", owner.fullName());
            payload.put("ownerEmail", owner.email());
            payload.put("ownerPhone", owner.phone());
            payload.put("ingestionSource", "FSBO_BULK_UPLOAD");
            payload.put("propertyId", record.getId().toString());

            try {
                String jsonPayload = objectMapper.writeValueAsString(payload);
                kafkaTemplate.send(TOPIC_LEADS, record.getId().toString(), jsonPayload);
            } catch (Exception e) {
                log.error("Error serializando", e);
            }
        }
    }
}