package com.inmohub.fsbo.ingestor.service.domain.ports;

import com.inmohub.fsbo.ingestor.service.domain.models.FsboBatch;

/**
 * Puerto de salida para publicar eventos de lead de propietario FSBO.
 */
public interface ILeadEventPublisher {
    void publishOwnerAsLeadEvent(FsboBatch batch);
}
