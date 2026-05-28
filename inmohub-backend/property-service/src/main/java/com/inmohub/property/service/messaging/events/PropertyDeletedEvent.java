package com.inmohub.property.service.messaging.events;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Evento de dominio emitido cuando se elimina una propiedad del sistema.
 * Notifica a otros servicios del ecosistema sobre la baja del inmueble
 * para mantener la consistencia de datos distribuidos.
 */
public record PropertyDeletedEvent(
        String eventType,
        UUID propertyId,
        LocalDateTime timestamp
) {

    public PropertyDeletedEvent {
        if (eventType == null || eventType.isBlank()) throw new IllegalArgumentException("eventType no puede ser nulo");
        if (propertyId == null) throw new IllegalArgumentException("propertyId no puede ser nulo");
        if (timestamp == null) throw new IllegalArgumentException("timestamp no puede ser nulo");
    }
    public static PropertyDeletedEvent of(UUID propertyId) {
        return new PropertyDeletedEvent("PROPERTY_DELETED", propertyId, LocalDateTime.now());
    }
}
