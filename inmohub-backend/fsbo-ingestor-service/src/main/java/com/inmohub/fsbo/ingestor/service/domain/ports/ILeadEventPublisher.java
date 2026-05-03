package com.inmohub.fsbo.ingestor.service.domain.ports;

import com.inmohub.fsbo.ingestor.service.domain.models.FsboBatch;

public interface ILeadEventPublisher {
    void publishOwnerAsLeadEvent(FsboBatch batch);
}
