package com.inmohub.fsbo.ingestor.service.domain.ports;

import com.inmohub.fsbo.ingestor.service.domain.models.FsboRecord;

public interface ILeadEventPublisher {
    void publishLeadCreatedEvent(FsboRecord record);
}
