package com.inmohub.fsbo.ingestor.service.domain.ports;

import com.inmohub.fsbo.ingestor.service.domain.models.PropertyRecord;

import java.util.UUID;

public interface ILeadEventPublisher {
    void publishLeadCreatedEvent(UUID ownerId);
}
