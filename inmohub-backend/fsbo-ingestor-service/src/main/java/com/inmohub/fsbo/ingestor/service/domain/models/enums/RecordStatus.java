package com.inmohub.fsbo.ingestor.service.domain.models.enums;

/**
 * Estados posibles de un registro de propiedad: pendiente, procesado, duplicado o error.
 */
public enum RecordStatus {
    PENDING,
    PROCESSED,
    DUPLICATED,
    ERROR
}
