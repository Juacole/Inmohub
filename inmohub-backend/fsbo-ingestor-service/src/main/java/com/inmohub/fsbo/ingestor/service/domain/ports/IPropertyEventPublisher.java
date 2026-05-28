package com.inmohub.fsbo.ingestor.service.domain.ports;

import com.inmohub.fsbo.ingestor.service.domain.models.FsboBatch;

/**
 * Puerto de salida para publicar eventos de creacion masiva de propiedades.
 */
public interface IPropertyEventPublisher {
    void publishBulkProperties(FsboBatch fsboBatch);
}
