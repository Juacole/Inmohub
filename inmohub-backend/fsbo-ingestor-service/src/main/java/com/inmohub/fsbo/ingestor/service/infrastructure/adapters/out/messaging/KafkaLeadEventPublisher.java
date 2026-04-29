package com.inmohub.fsbo.ingestor.service.infrastructure.adapters.out.messaging;

import com.inmohub.fsbo.ingestor.service.domain.models.OwnerDetails;
import com.inmohub.fsbo.ingestor.service.domain.ports.ILeadEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class KafkaLeadEventPublisher implements ILeadEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC_LEADS = "lead.events";

    @Override
    public void publishOwnerAsLeadEvent(OwnerDetails ownerDetails) {
        Map<String, Object> payload = new HashMap<>();

        payload.put("eventType", "FSBO_OWNER_BULK_UPLOAD");
        payload.put("ownerId", ownerDetails.ownerId().toString());
        payload.put("ownerName", ownerDetails.fullName());
        payload.put("ownerEmail", ownerDetails.email());
        payload.put("ownerPhone", ownerDetails.phone());
        payload.put("ingestionSource", "FSBO_BULK_UPLOAD");

        kafkaTemplate.send(TOPIC_LEADS, ownerDetails.ownerId().toString(), payload);
    }
}