package com.inmohub.lead.service.infrastructure.adapters.out.messaging;

import com.inmohub.lead.service.domain.model.Lead;
import com.inmohub.lead.service.domain.ports.ILeadEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaLeadEventPublisher implements ILeadEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "lead.events";

    @Override
    public void publishLeadCreatedEvent(Lead lead) {
        kafkaTemplate.send(TOPIC, lead.getId().toString(), "Lead creado: " + lead.getEmail());
    }
}