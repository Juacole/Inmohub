package com.inmohub.fsbo.ingestor.service.infrastructure.adapters.out.messaging;

import com.inmohub.fsbo.ingestor.service.domain.ports.ILeadEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class KafkaLeadEventPublisher implements ILeadEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC_LEADS = "lead.events";

    @Override
    public void publishOwnerAsLeadEvent(UUID ownerId) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("eventType", "FSBO_OWNER_BULK_UPLOAD");
        payload.put("ownerId", ownerId.toString());
        payload.put("source", "FSBO");
        payload.put("message", "El propietario ha realizado una carga masiva de inmuebles.");

        kafkaTemplate.send(TOPIC_LEADS, ownerId.toString(), payload);
    }
}