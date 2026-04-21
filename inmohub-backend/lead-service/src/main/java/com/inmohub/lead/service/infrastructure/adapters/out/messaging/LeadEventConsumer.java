package com.inmohub.lead.service.infrastructure.adapters.out.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class LeadEventConsumer {

    @KafkaListener(topics = "lead.events", groupId = "lead-service-group")
    public void consume(String message) {
        // TODO: Consumer provicional, su utilidad es futura, con la implementación de nuevos microservicios
        System.out.println("Evento recibido: " + message);
    }
}